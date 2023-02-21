package oneclientmanyfile.server;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import oneclientmanyfile.User;

import java.util.List;

public class isDecoder extends ByteToMessageDecoder {

    public static boolean br = true;
    private long contentSumLength = 0;
    private long fileContentlength = 0;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        if (br) {


            int mark = 0;

            byteBuf.markReaderIndex();

            int kd = byteBuf.readableBytes();

            if (kd < 4) {
                byteBuf.resetReaderIndex();
                return;
            }
            mark = byteBuf.readInt();

            if (mark == 1) {

                kd = byteBuf.readableBytes();

                if (kd < 4) {
                    byteBuf.resetReaderIndex();
                    return;
                }

                int directoryLength = byteBuf.readInt(); //文件夹名长度

                kd = byteBuf.readableBytes();

                if (kd < directoryLength) {
                    byteBuf.resetReaderIndex();
                    return;
                }

                byte [] bytes = new byte[directoryLength];

                byteBuf.readBytes(bytes);  //因为名字的接收方式用的bytes  他可以获取到传的值 但是没减去 所以要后续再手动读一次名字接收的长度

                User user = new User();
                user.setDirectorname(new String(bytes));
                list.add(user);

            } else {
                kd = byteBuf.readableBytes();

                if (kd < 4) {
                    byteBuf.resetReaderIndex();
                    return;
                }

                int filenamelenth = byteBuf.readInt();

                kd = byteBuf.readableBytes();

                if (kd < filenamelenth) {
                    byteBuf.resetReaderIndex();
                    return;
                }

                byte [] bytes = new byte[filenamelenth]; //文件名
                byteBuf.readBytes(bytes);

                kd = byteBuf.readableBytes();

                if (kd < 8) {
                    byteBuf.resetReaderIndex();
                    return;
                }

                long filelength = byteBuf.readLong();

                User user = new User();
                user.setName(new String(bytes));
                user.setContentLength(filelength);

                contentSumLength = filelength; //将文件长度赋给总长度

                if (filelength != 0) {
                    br = false;
                }

                list.add(user);

            }


        } else {
            if (byteBuf.readableBytes() > contentSumLength - fileContentlength){ //当前文件字节小于 可读字节 就只接受当文件字节数量
                byte [] bytes = new byte[Integer.valueOf(String.valueOf(contentSumLength - fileContentlength))];
                byteBuf.readBytes(bytes);

                contentSumLength = 0;
                fileContentlength = 0;
                list.add(bytes);

            } else {
                byte [] bytes = new byte[byteBuf.readableBytes()];
                fileContentlength = fileContentlength + bytes.length;  //获取到每次累加长度
                byteBuf.readBytes(bytes);   //这个是用来类似标识的 如果读取的长度 不对还会再读取 不是用来传的
                if (fileContentlength == contentSumLength) {
                    fileContentlength = 0;
                    contentSumLength = 0;
                }

                list.add(bytes);



            }
        }
    }
}
