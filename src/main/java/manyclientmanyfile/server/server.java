package manyclientmanyfile.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;


public class server {
    public void bind(int port) throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1); //用来处理连接请求  默认是cpu的俩倍
        EventLoopGroup workerGroup = new NioEventLoopGroup(20); //这个用来读写 异步线程
        ServerBootstrap serverBootstrap = new ServerBootstrap();  //服务端辅助启动类
        try {

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) //设置管道模式
                    .option(ChannelOption.SO_BACKLOG, 1024) //1024最大连接数量 操作1024发的连接请求不接收需要重新请求
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            //使用加码解码器
                            p.addLast(new ByteArrayEncoder());//byte数组加密类
                            p.addLast(new isDecoder());//使用自定义解密类
                            p.addLast(new serverHandler());//处理器


                        }
                    }); //也可以把内部类放这里

            //ChannelFuture 函数回调 可以在中途做其他事情 不需要等待
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();//.channel.()sycn() 直到管道关闭，才会继续往下执行，否则一直线程阻塞 //可以用来服务端还没发完 客户端就结束了
            channelFuture.channel().closeFuture().sync(); //
        } finally {
            //退出 释放线程池资源
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
        }


    }


    public static void main(String[] args) throws Exception {
        new server().bind(6299);


    }
}
