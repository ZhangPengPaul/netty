package io.paul.example.factorial;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * User: Paul Zhang
 * Date: 14-10-13
 * Time: 下午6:36
 */
public class FactorialClient {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8322"));
    static final int COUNT = Integer.parseInt(System.getProperty("count", "10"));

    public static void main(String[] args) throws Exception {
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new FactorialClientInitializer(sslCtx));

            ChannelFuture f = b.connect(HOST, PORT).sync();

            FactorialClientHandler handler = (FactorialClientHandler) f.channel().pipeline().last();

            System.err.format("(Client)Factorial of %,d is: %,d", COUNT, handler.getFactorial());


        } finally {
            group.shutdownGracefully();
        }
    }
}
