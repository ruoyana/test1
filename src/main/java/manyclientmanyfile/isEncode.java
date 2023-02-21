package manyclientmanyfile;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import manyclientmanyfile.User;


public class isEncode extends MessageToByteEncoder<User> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, User user, ByteBuf byteBuf) throws Exception {

        if (user.getDirectornameLength() != 0) {  //1 文件夹
            byteBuf.writeInt(1);
            byteBuf.writeInt(user.getDirectornameLength());
            byteBuf.writeBytes(user.getDirectorname().getBytes());

        } else {  //2 文件

            byteBuf.writeInt(2);
            byteBuf.writeInt(user.getNameLength());
            byteBuf.writeBytes(user.getName().getBytes());
            byteBuf.writeLong(user.getContentLength());

        }

    }
}
