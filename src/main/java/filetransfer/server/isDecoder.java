package filetransfer.server;

import filetransfer.User;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class isDecoder extends ByteToMessageDecoder {


    public static boolean br = true;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        System.out.println("进来了");
        if (br){
            byteBuf.markReaderIndex(); //标识

            int mark = byteBuf.readableBytes();
            if (mark < 4) {
                byteBuf.resetReaderIndex(); //退回
                return;
            }
            int filenamlength = byteBuf.readInt();

            mark = byteBuf.readableBytes();
            if (mark < filenamlength) {
                byteBuf.resetReaderIndex(); //退回
                return;
            }

            byte [] bytes = new byte[filenamlength];
            byteBuf.readBytes(bytes);

            mark = byteBuf.readableBytes();
            if (mark < 8) {
                byteBuf.resetReaderIndex();
                return;
            }
            long fileContentLength = byteBuf.readLong();

            User user = new User();
            user.setNameLength(filenamlength);
            user.setName(new String(bytes));
            user.setContentLength(fileContentLength);

            br = false; //执行到这就标识头部消息接完了 接下来就只会进入else 接文件内容了


            list.add(user); //发送到server read




        } else {
            //之后来都是客户端传的内容 所以是byte 并且不会经过编码器
            //文件内容
            byte [] bytes = new byte[byteBuf.readableBytes()]; //1024
            byteBuf.readBytes(bytes);
            list.add(bytes);

        }

    }
}
