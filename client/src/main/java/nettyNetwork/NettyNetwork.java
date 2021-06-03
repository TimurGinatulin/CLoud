package nettyNetwork;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import nettyNetwork.handlers.Callback;
import nettyNetwork.handlers.MessageHandler;

public class NettyNetwork implements Runnable {
    private static NettyNetwork network;
    private SocketChannel clientChannel;
    private Callback callback;

    private NettyNetwork() {
    }
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public static NettyNetwork getInstance() {
        if (network == null)
            network = new NettyNetwork();
        return network;
    }

    @Override
    public void run() {
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            clientChannel = socketChannel;
                            clientChannel.pipeline().
                                    addLast(
                                            new ObjectEncoder(),
                                            new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                            new MessageHandler(callback));
                        }
                    });
            ChannelFuture future = bootstrap.connect("localhost", 2021).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }

    public void writeMassage(Object message) {
        clientChannel.writeAndFlush(message);
    }
}
