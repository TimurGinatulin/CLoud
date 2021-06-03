package net.handlers;

import utils.Decoder.MessageDecoder;
import connectors.databaseConnector.Authorization;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.Message;
import net.UserContainer;

import java.io.File;

public class MessageHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        if (UserContainer.containsId(msg.getIdUser())) {
            ctx.writeAndFlush(MessageDecoder.decode(msg));
        } else
            auth(ctx, msg);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        sendByServer(ctx, "Please enter Username and password");
        super.channelRegistered(ctx);
    }

    private void sendByServer(ChannelHandlerContext ctx, String content) {
        ctx.writeAndFlush(Message.builder()
                .author("Server")
                .content(content)
                .sentAt(System.currentTimeMillis()).build());
    }

    private void auth(ChannelHandlerContext ctx, Message message) {
        String[] usrMsgArray = message.getContent().trim().split(" ");
        if (usrMsgArray.length > 1) {
            Message auth = Authorization.authUser(usrMsgArray[0], usrMsgArray[1]);
            if (auth != null) {
                UserContainer.addUserId(auth.getIdUser());
                if (createDir(auth.getCurrentPath()))
                    auth.setContent("create dir");
                ctx.writeAndFlush(auth);
            } else
                sendByServer(ctx, "Uncorrected Username or(and) password");
        } else
            sendByServer(ctx, "Please enter message in format:\n {username password}");
    }

    private boolean createDir(String path) {
        File file = new File(path);
        if (!file.exists())
            return file.mkdirs();
        else
            return false;
    }
}
