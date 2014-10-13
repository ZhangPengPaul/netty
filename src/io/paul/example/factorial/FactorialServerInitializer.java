package io.paul.example.factorial;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.ssl.SslContext;


/**
 * User: Paul Zhang
 * Date: 14-10-13
 * Time: 下午5:51
 */
public class FactorialServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public FactorialServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(socketChannel.alloc()));
        }

//        pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
//        pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));

        pipeline.addLast(new BigIntegerDecoder());
        pipeline.addLast(new NumberEncoder());

        pipeline.addLast(new FactorialServerHandler());
    }
}
