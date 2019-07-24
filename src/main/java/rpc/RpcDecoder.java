package rpc;

import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {
    private Class<?> rpcBodyClass;

    public RpcDecoder(Class<?> rpcBodyClass) {
        this.rpcBodyClass = rpcBodyClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        var input = new ObjectDecoderInputStream(new ByteBufInputStream(in));
        var obj = input.readObject();
        input.close();
        list.add(obj);
    }
}
