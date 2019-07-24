package rpc.client;

import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.RpcRequest;
import rpc.RpcResponse;

import java.util.HashMap;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientHandler.class);
    private HashMap<String, ChannelPromise> promiseMap = new HashMap<>();
    private HashMap<String, RpcResponse> responseMap = new HashMap<>();
    private volatile Channel channel;
    private RpcResponse response;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channel = ctx.channel();
        LOGGER.info("Channel is active.");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        var promise = promiseMap.get(response.getRequestId());
        responseMap.put(response.getRequestId(), response);
        promise.setSuccess();
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
        LOGGER.info("Channel is registered.");
    }

    public Object getResult(String requestId) {
        return responseMap.get(requestId).getResult();
    }

    public ChannelPromise sendRequest(RpcRequest request) throws InterruptedException {
        while (channel == null || !channel.isActive()) {
            Thread.sleep(1000);
        }
        var promise = this.channel.writeAndFlush(request).channel().newPromise();
        this.promiseMap.put(request.getRequestId(), promise);
        return promise;
    }
}
