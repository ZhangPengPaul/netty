package io.paul.example.factorial;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.math.BigInteger;
import java.util.List;

/**
 * User: Paul Zhang
 * Date: 14-10-13
 * Time: 下午5:56
 */
public class BigIntegerDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> objects) throws Exception {
        if (byteBuf.readableBytes() < 5) {
            return;
        }

        byteBuf.markReaderIndex();

        int magicNumber = byteBuf.readUnsignedByte();

        if (magicNumber != 'F') {
            byteBuf.resetReaderIndex();
            throw new CorruptedFrameException("Invalid magic number: " + magicNumber);
        }

        int dataLength = byteBuf.readInt();
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] decoded = new byte[dataLength];
        byteBuf.readBytes(decoded);

        objects.add(new BigInteger(decoded));
    }
}
