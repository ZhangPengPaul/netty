package io.paul.example.factorial;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.math.BigInteger;

/**
 * User: Paul Zhang
 * Date: 14-10-13
 * Time: 下午5:39
 */
public class FactorialServerHandler extends SimpleChannelInboundHandler<BigInteger> {

    private BigInteger lastMultiplier = new BigInteger("1");
    private BigInteger factorial = new BigInteger("1");

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BigInteger bigInteger) throws Exception {
        lastMultiplier = bigInteger;
        factorial = factorial.multiply(bigInteger);
        channelHandlerContext.writeAndFlush(factorial);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.err.printf("Factorial of %,d is: %,d%n", lastMultiplier, factorial);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
