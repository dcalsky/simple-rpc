package rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import rpc.*;

import java.util.concurrent.ConcurrentHashMap;

public class RpcServer {
    private int port;
    private ConcurrentHashMap<String, Object> serviceMap = new ConcurrentHashMap<>();
    private Objenesis objenesis = new ObjenesisStd(true);

    public RpcServer(int port) {
        this.port = port;
    }

    public void addService(Class<?> serviceClass) {
        var instance = objenesis.newInstance(serviceClass);
        this.serviceMap.put(instance.getClass().getAnnotation(RpcService.class).value().getName(), instance);
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4));
                            p.addLast(new RpcDecoder(RpcRequest.class));
                            p.addLast(new RpcEncoder(RpcResponse.class));
                            p.addLast(new RpcServerHandler(serviceMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
