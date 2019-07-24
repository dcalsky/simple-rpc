package rpc.client;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

public class ObjectProxy implements InvocationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectProxy.class);
    private Class<?> serviceClass;

    public ObjectProxy(Class<?> serviceClass) {
        this.serviceClass = serviceClass;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }
        var request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setMethodName(method.getName());
        request.setClassName(method.getDeclaringClass().getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        var handler = ConnectManager.getInstance().getAvailableHandler();
        handler.sendRequest(request).await();
        return handler.getResult(request.getRequestId());
    }
}
