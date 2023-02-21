package oneclientmanyfile.client;


import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.stream.ChunkedFile;
import oneclientmanyfile.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public class clientHandler extends ChannelInboundHandlerAdapter {


    private static String filenamepath = "D:\\JavaLearn";


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        File file = new File(filenamepath);
        digui(file, ctx);


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void digui(File file, ChannelHandlerContext channelHandlerContext) {
      //递归发送文件头信息
        File[] files = file.listFiles();  //listFiles是会将全路径赋值递归的 D:\JavaLearn\Springmvc2\Webcontent\WEB-INF\qu.jsp  所以下面fore的filer能直接filer。length获取到文件长度
        for (File filer :
                files) {
            if (filer.isDirectory()) {

                User user = new User();

                String directory = filer.getPath().replace(filenamepath, ""); //获取名字 不是获取全路径名字
                System.out.println(directory);
                user.setDirectornameLength(directory.getBytes().length);
                user.setDirectorname(directory);

                channelHandlerContext.writeAndFlush(user);
                digui(filer, channelHandlerContext);

            } else {

                String filename = filer.getPath().replace(filenamepath, "");
                System.out.println(filename);

                User user = new User();
                user.setNameLength(filename.getBytes().length);
                user.setName(filename);
                user.setContentLength(filer.length());
                channelHandlerContext.writeAndFlush(user);

                if (filer.length() > 0) {

                    try {
                        RandomAccessFile randomAccessFile  = new RandomAccessFile(filer.getPath(), "r");;

                       //这个可以自动帮我们传输

                        channelHandlerContext.writeAndFlush(new ChunkedFile(randomAccessFile)).addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                                channelFuture.channel().close();
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }


            }
        }


    }


}
