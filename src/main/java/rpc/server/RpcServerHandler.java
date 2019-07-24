package rpc.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.RpcRequest;
import rpc.RpcResponse;
import rpc.client.ObjectProxy;

import java.util.concurrent.ConcurrentHashMap;

public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectProxy.class);
    private ConcurrentHashMap<String, Object> serviceMap;

    RpcServerHandler(ConcurrentHashMap<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        var response = handleRequest(request);
        LOGGER.info("Get a request");
        ctx.writeAndFlush(response);
    }


    private RpcResponse handleRequest(RpcRequest request) {
        var response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        var service = serviceMap.get(request.getClassName());
        if (service == null) {
            response.setError("Service not found.");
            return response;
        }
        var clazz = service.getClass();
        try {
            var method = clazz.getMethod(request.getMethodName(), request.getParameterTypes());
            method.setAccessible(true);
            var result = method.invoke(service, request.getParameters());
            response.setResult(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
