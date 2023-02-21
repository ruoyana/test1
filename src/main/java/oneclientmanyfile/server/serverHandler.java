package oneclientmanyfile.server;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import oneclientmanyfile.User;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class serverHandler extends ChannelInboundHandlerAdapter {

    private FileOutputStream fileOutputStream; //输出流对象
    private BufferedOutputStream bufferedOutputStream;  //这个流具有 自动内容追加功能 不会覆盖以前的内容
    private static String filelj = "C:\\Users\\ruoyan\\Desktop\\新建文件夹";   //输出路径

    private long filecontenLength = 0;
    private long fileSumcontent = 0;


    //管道连接成功是会进来
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端连接成功");

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof User) {  //只有第一次接头信息是User类型 之后都是byte类型接收内容了
            User user = (User) msg;
            if (user.getDirectorname() != null) {  //getDirectorname不等于null 表示这个文件夹
                File file = new File(filelj + user.getDirectorname());
                System.out.println("file==  " + file.getPath());
                if (!file.exists()) {
                    file.mkdirs(); //创建文件
                }
            } else {  //文件

                fileSumcontent = user.getContentLength();

                File file = new File(filelj + user.getName());
                System.out.println("file==  " + file.getPath());
                if (!file.exists()) {
                    file.createNewFile(); //创建文件
                }

                if (user.getContentLength() > 0) {
                    fileOutputStream = new FileOutputStream(file);
                    bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                }
            }
            return;   //第一次进来读完头信息就 离开
        }

        byte [] bytes = (byte[]) msg;
        filecontenLength = filecontenLength + bytes.length;
        bufferedOutputStream.write(bytes,0,bytes.length); //读取长度为bytes 从0开始 每次读取length个
        bufferedOutputStream.flush();

        if (filecontenLength == fileSumcontent) {  //总文件长度 == 累加长度
            filecontenLength = 0;
            fileSumcontent = 0;
            bufferedOutputStream.close();
            isDecoder.br = true;  //设置为true 让后面的继续接收
        }





    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override  //连接完之后关闭流
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(1);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


}
