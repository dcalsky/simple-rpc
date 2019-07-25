package rpc.client;

import java.io.IOException;
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
