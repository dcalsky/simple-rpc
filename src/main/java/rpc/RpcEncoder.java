package rpc;

import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder {
    private Class<?> clazz;

    public RpcEncoder(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf out) throws Exception {
        var bs = new ObjectEncoderOutputStream(new ByteBufOutputStream(out));
        bs.writeObject(o);
        bs.close();
    }
}
