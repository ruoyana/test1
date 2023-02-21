package everyclientonefile.client1;


import everyclientonefile.User;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.stream.ChunkedFile;

import java.io.File;
import java.io.RandomAccessFile;

public class ClientHandler1 extends ChannelInboundHandlerAdapter {

    private String basepath = "D:\\JavaLearn";
    private String filePath;  //文件路径
    private int mark;

    public ClientHandler1(int mark,String filePath){
        this.mark = mark;
        this.filePath = filePath;

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        User user = new User();
        user.setMark(mark);

        if (user.getMark() == 2){ //表示文件
            user.setFilecontentLength(new File(filePath).length());
            System.out.println("filelength== " + user.getFilecontentLength());
        }



        user.setFilename(filePath.replace(basepath, ""));
        user.setFilenameLength(filePath.replace(basepath, "").getBytes().length);

        System.out.println(user.getFilename());
        System.out.println(user.getFilenameLength());

        ctx.writeAndFlush(user);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String js = (String) msg;

        if (js.equals("j")) {
            System.out.println("eqj");
            ctx.close(); //这是为了关闭管道,如果管道不关.线程一直不会结束
        } else  if (js.equals("w")){  //返回w表示有内容 需要传输
            System.out.println("eqw");

            RandomAccessFile randomAccessFile  = new RandomAccessFile(filePath, "r");;

            //这个可以自动帮我们传输

            ctx.writeAndFlush(new ChunkedFile(randomAccessFile)).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                channelFuture.channel().close(); //为什么每个客户端传一个文件就能发送就关闭能? 是因为他是每个文件就是一个管道
                                                                 //关闭了管道后 但不会关闭递归client(代码原因) client会继续发送下个文件 下个文件又是管道了 直到发完
                }
            });



        }






    }
}
