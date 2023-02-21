package filetransfer.server;


import filetransfer.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class serverHandler extends ChannelInboundHandlerAdapter {

    private FileOutputStream fileOutputStream; //输出流对象
    private BufferedOutputStream bufferedOutputStream;  //这个流具有 自动内容追加功能 不会覆盖以前的内容
    private static String filelj =  "C:\\Users\\ruoyan\\Desktop\\新建文件夹\\";   //输出路径

    //管道连接成功是会进来
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端连接成功");



    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof User) {  //只有第一次接头信息是User类型 之后都是byte类型接收内容了
            String a = ((User) msg).getName();
            System.out.println(a);

            File file = new File(filelj +  a);
            System.out.println("file " + file.getPath());
            if (!file.exists()){
                file.createNewFile(); //创建文件
            }

            fileOutputStream = new FileOutputStream(file); //这里指定了文件位置
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream); //将文件加入流

                ctx.writeAndFlush("ok".getBytes()); //返回一条消息给客户端 触发内容发送 也表示头接完了
            return ; //return 的作用是 接完头信息后 就不往下接收了 不然就接收到内容了

        }

        System.out.println("到");
        byte [] bytes = (byte[])msg; //因为后面接的都是byte类了 msg就说内容
        System.out.println(bytes.length);
        bufferedOutputStream.write(bytes,0, bytes.length); //追加流 因为fileOutputStream指定了文件位置 他就根据这个文件位置追加内容



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
