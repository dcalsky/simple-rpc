package rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import rpc.RpcDecoder;
import rpc.RpcEncoder;
import rpc.RpcRequest;
import rpc.RpcResponse;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConnectManager {
    private static final String HOST = System.getProperty("host", "127.0.0.1");
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16,
            600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
    private static ConnectManager instance;
    private RpcClientHandler handler;

    private ConnectManager() {
    }

    public static ConnectManager getInstance() {
        if (instance == null) {
            synchronized (ConnectManager.class) {
                if (instance == null) {
                    instance = new ConnectManager();
                }
            }
        }
        return instance;
    }

    private void addHandler(RpcClientHandler handler) {
        this.handler = handler;
    }

    public RpcClientHandler getAvailableHandler() throws InterruptedException {
        while (this.handler == null) {
            Thread.sleep(1000);
        }
        return this.handler;
    }

    public void connect(int port) throws InterruptedException, IOException {
//        threadPoolExecutor.submit(() -> {
        var group = new NioEventLoopGroup();
        var b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        final var p = ch.pipeline();
//                        p.addLast("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(RpcResponse.class.getClassLoader())));
//                        p.addLast("encoder", new ObjectEncoder());
                        p.addLast(new RpcDecoder(RpcResponse.class));
                        p.addLast(new RpcEncoder(RpcRequest.class));
                        p.addLast(new RpcClientHandler());
                    }
                });

        b.connect(HOST, port).addListener((ChannelFutureListener) (channelFuture) -> {
            RpcClientHandler handler = channelFuture.channel().pipeline().get(RpcClientHandler.class);
            addHandler(handler);
        });
//        });


    }

}
