package everyclientonefile.server;


import everyclientonefile.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class serverHandler extends ChannelInboundHandlerAdapter {

    private FileOutputStream fileOutputStream; //输出流对象
    private BufferedOutputStream bufferedOutputStream;  //这个流具有 自动内容追加功能 不会覆盖以前的内容
    private static String filelj = "C:\\Users\\ruoyan\\Desktop\\新建文件夹\\";   //输出路径

    private Map<ChannelHandlerContext, Long> fileSumcontent = new HashMap<>();
    private  Map<ChannelHandlerContext, Long>  filecontenLength = new HashMap<>();




    //管道连接成功是会进来
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端连接成功");

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof User) {  //只有第一次接头信息是User类型 之后都是byte类型接收内容了
            User user = (User) msg;
            if (user.getMark() == 1) {  //1 文件夹
                File file = new File(filelj + user.getFilename());
                System.out.println("Dircetor==  " + file.getPath());
                if (!file.exists()) {
                    file.mkdirs(); //创建文件
                }

                ctx.writeAndFlush("j");
                ctx.close();
                return;


            } else {  //文件

                File file = new File(filelj + user.getFilename());
                System.out.println("file==  " + file.getPath());
                if (!file.exists()) {
                    System.out.println("创建文件");
                    file.createNewFile(); //创建文件
                }


                fileSumcontent.put(ctx,user.getFilecontentLength()); //文件内容长度
                System.out.println("filesum== " + fileSumcontent.get(ctx));

                String test = "w";
                if (fileSumcontent.get(ctx)== 0){ //文件内容长度==0 就是没东西需要传 直接和文件夹一样结束就好了
                    test = "j";
                    ctx.writeAndFlush(test.getBytes());
                    ctx.close();
                    return;
                }
                System.out.println("test== " + test);

                if (fileSumcontent.get(ctx) > 0) {
                    fileOutputStream = new FileOutputStream(file);
                    bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                }

                ctx.writeAndFlush(test.getBytes());

                return;   //第一次进来读完头信息就 离开
            }


        } else {



        byte [] bytes = (byte[]) msg;

        if (filecontenLength.get(ctx) == null) {
            filecontenLength.put(ctx,0L);
        }

        if (fileSumcontent.get(ctx) == null) {
            fileSumcontent.put(ctx,0L);
        }



        filecontenLength.put(ctx,filecontenLength.get(ctx) + bytes.length); //累加内容

        bufferedOutputStream.write(bytes,0,bytes.length); //读取长度为bytes 从0开始 每次读取length个
        bufferedOutputStream.flush();

        if (filecontenLength.get(ctx).equals(fileSumcontent.get(ctx))) {  //总文件长度 == 累加长度

            bufferedOutputStream.close();
            ctx.writeAndFlush("j");  //接完了就关闭
            ctx.close();
//            filecontenLength.put(ctx,0L);
//            fileSumcontent.put(ctx,0L);
         }
        }





    }



    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override  //连接完之后关闭流
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println(1);
//        bufferedOutputStream.flush();
//        bufferedOutputStream.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


}
