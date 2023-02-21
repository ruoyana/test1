package everyclientonefile;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class isEncode extends MessageToByteEncoder<User> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, User user, ByteBuf byteBuf) throws Exception {


        System.out.println(user.getMark());
        if (user.getMark() == 1) { //文件夹
            byteBuf.writeInt(1);
            byteBuf.writeInt(user.getFilenameLength());
            byteBuf.writeBytes(user.getFilename().getBytes());

        } else {   //文件
            byteBuf.writeInt(2);
            byteBuf.writeInt(user.getFilenameLength());
            byteBuf.writeBytes(user.getFilename().getBytes());
            byteBuf.writeLong(user.getFilecontentLength());
        }


    }
}
