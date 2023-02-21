package filetransfer.client;

import filetransfer.User;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class isEncode extends MessageToByteEncoder<User> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, User user, ByteBuf byteBuf) throws Exception {
                byteBuf.writeInt(user.getNameLength());
                byteBuf.writeBytes(user.getName().getBytes());
                byteBuf.writeLong(user.getContentLength());



    }
}
