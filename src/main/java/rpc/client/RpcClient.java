package rpc.client;

import example.rpc.Client;
import example.rpc.GreetService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import rpc.RpcDecoder;
import rpc.RpcEncoder;
import rpc.RpcRequest;
import rpc.RpcResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Proxy;

public class RpcClient {
    private int port;

    public <T> T create(Class<T> serviceInterface) {
        return (T) Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class<?>[]{serviceInterface},
                new ObjectProxy(serviceInterface)
        );
    }

    public RpcClient(int port) throws InterruptedException, IOException {
        this.port = port;
        ConnectManager.getInstance().connect(this.port);
    }
}
