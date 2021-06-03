package nettyNetwork.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.Message;

public class MessageHandler extends SimpleChannelInboundHandler<Message> {
    private final Callback callback;

    public MessageHandler(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) {
        callback.processMessage(message);
    }
}
