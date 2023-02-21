package filetransfer.client;

import filetransfer.User;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.stream.ChunkedFile;

import java.io.File;
import java.io.RandomAccessFile;


public class clientHandler extends ChannelInboundHandlerAdapter {

    private String filename;

    private static String filenames = "C:\\linux\\linuxIso\\CentOS-8.4.2105-x86_64-dvd1.iso";

    public clientHandler(){
        filename = "CentOS-8.4.2105-x86_64-dvd1.iso";  //这个是用来提供 文件名 和 文件名长度
    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        User user = new User();
        user.setNameLength(filename.getBytes().length);
        user.setName(filename);

        File file = new File(filenames);
        System.out.println("file== " +  file.length());

        user.setContentLength(file.length());



        ctx.writeAndFlush(user);

    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("客户端读");

        //客户端获取读路径 r代表read读 可以添加w 写
        RandomAccessFile randomAccessFile = new RandomAccessFile(filenames,"r");  //这个可以自动帮我们传输

        //发送字节 自动帮我们发送每次 1024 可以调
        ctx.writeAndFlush(new ChunkedFile(randomAccessFile)).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                channelFuture.channel().close();
            }
        });


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {

    }


}
