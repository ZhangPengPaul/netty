package io.paul.example.factorial;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.math.BigInteger;

/**
 * User: Paul Zhang
 * Date: 14-10-13
 * Time: 下午6:08
 */
public class NumberEncoder extends MessageToByteEncoder<Number> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Number number, ByteBuf byteBuf) throws Exception {
        BigInteger v;

        if (number instanceof BigInteger) {
            v = (BigInteger) number;
        } else {
            v = new BigInteger(String.valueOf(number));
        }

        byte[] data = v.toByteArray();
        int dataLength = data.length;

        byteBuf.writeByte((byte) 'F');
        byteBuf.writeInt(dataLength);
        byteBuf.writeBytes(data);
    }
}
