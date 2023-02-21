package manyclientmanyfile.server;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import manyclientmanyfile.User;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class isDecoder extends ByteToMessageDecoder {

    public static Map<ChannelHandlerContext, Boolean> br = new HashMap<>();   //channelHandlelrContext每个人都有一个独立的
    private  Map<ChannelHandlerContext, Long> contentSumLength = new HashMap<>();
    private  Map<ChannelHandlerContext, Long>  fileContentlength = new HashMap<>();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

           //三元表达式 在if里最终结果是看赋值的值来判断的 不是看条件自己判断
        if (br.get(channelHandlerContext) == null ? true : br.get(channelHandlerContext)) { //每个客户端的文件 第一次进来都是null 进来后都会赋一次值


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

                long filelength = byteBuf.readLong();  //文件内容长度

                User user = new User();
                user.setName(new String(bytes));
                user.setContentLength(filelength);

//                contentSumLength = filelength; //将文件长度赋给总长度
                contentSumLength.put(channelHandlerContext,filelength);  //put设值
                System.out.println("con== " + contentSumLength.get(channelHandlerContext));

                list.add(user);

                if (filelength != 0) {
                    br.put(channelHandlerContext,false);
                }



            }


        } else {
            System.out.println("进来没");
             if (contentSumLength.get(channelHandlerContext) == null){
                 contentSumLength.put(channelHandlerContext,0L);
             }

            if (fileContentlength.get(channelHandlerContext) == null){
                fileContentlength.put(channelHandlerContext,0L);
            }


            if (byteBuf.readableBytes() > contentSumLength.get(channelHandlerContext) - fileContentlength.get(channelHandlerContext)){ //当前文件字节小于 可读字节 就只接受当文件字节数量
                byte [] bytes = new byte[Integer.valueOf(String.valueOf(contentSumLength.get(channelHandlerContext) - fileContentlength.get(channelHandlerContext)))];
                byteBuf.readBytes(bytes);

                list.add(bytes);
                contentSumLength.put(channelHandlerContext,0L);
                fileContentlength.put(channelHandlerContext,0L);

                br.put(channelHandlerContext, true);

            } else {
                byte [] bytes = new byte[byteBuf.readableBytes()];
                fileContentlength.put(channelHandlerContext,fileContentlength.get(channelHandlerContext) + bytes.length);
//                fileContentlength = fileContentlength + bytes.length;  //获取到每次累加长度
                byteBuf.readBytes(bytes);   //这个是用来类似标识的 如果读取的长度 不对还会再读取 不是用来传的
                System.out.println("filecon== " +  fileContentlength.get(channelHandlerContext) + "  filesum== " + contentSumLength.get(channelHandlerContext));
                if ( fileContentlength.get(channelHandlerContext).equals(contentSumLength.get(channelHandlerContext))) {
                    System.out.println("dyu");
                    contentSumLength.put(channelHandlerContext,0L);
                    fileContentlength.put(channelHandlerContext,0L);
                    br.put(channelHandlerContext, true);
                }

                list.add(bytes);



            }
        }
    }
}
