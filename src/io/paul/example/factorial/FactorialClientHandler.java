package io.paul.example.factorial;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * User: Paul Zhang
 * Date: 14-10-13
 * Time: 下午6:14
 */
public class FactorialClientHandler extends SimpleChannelInboundHandler<BigInteger> {

    private ChannelHandlerContext ctx;
    private int receivedMessages;
    private int next = 1;
    final BlockingQueue<BigInteger> answer = new LinkedBlockingDeque<>();

    public BigInteger getFactorial() {
        boolean interrupted = false;

        try {
            for (; ; ) {
                try {
                    return answer.take();
                } catch (InterruptedException e) {
                    interrupted = false;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        // send number
        sendNumbers();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, final BigInteger bigInteger) throws Exception {
        receivedMessages++;
        if (receivedMessages == FactorialClient.COUNT) {
            channelHandlerContext.close().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    boolean offered = answer.offer(bigInteger);
                    assert offered;
                }
            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void sendNumbers() {
        ChannelFuture future = null;

        for (int i = 0; i < 4096 && next <= FactorialClient.COUNT; i++) {
            future = ctx.write(Integer.valueOf(next));
            next++;
        }

        if (next <= FactorialClient.COUNT) {
            assert future != null;
            future.addListener(numberSender);
        }

        ctx.flush();

    }

    private final ChannelFutureListener numberSender = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                sendNumbers();
            } else {
                future.cause().printStackTrace();
                future.channel().close();
            }
        }
    };
}
