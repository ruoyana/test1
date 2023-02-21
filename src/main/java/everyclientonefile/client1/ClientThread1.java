package everyclientonefile.client1;



import everyclientonefile.isEncode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.stream.ChunkedWriteHandler;



public class ClientThread1 extends Thread {

    public  String filePath;
    private   int mark;

    public ClientThread1(String filePath,int mark) {
        this.mark = mark;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        try {
            new ClientThread1(filePath,mark).connect(6299,"192.168.31.140");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void connect(int port,String host)throws Exception {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap(); //客户端辅助启动类

        try {

            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                     .option(ChannelOption.TCP_NODELAY,true)
                     .handler(new ChannelInitializer<SocketChannel>() {

                         @Override
                         protected void initChannel(SocketChannel socketChannel) throws Exception {
                             ChannelPipeline p = socketChannel.pipeline();
                             //使用加码解码器
                             p.addLast(new ByteArrayEncoder());//byte数组加密类
                             p.addLast(new StringDecoder());//String jiehsou
                             p.addLast(new ChunkedWriteHandler());
                             p.addLast(new isEncode());//使用自定义解密类
                             p.addLast(new ClientHandler1(mark,filePath));//处理器


                         }
                     });

            ChannelFuture channelFuture = bootstrap.connect(host,port).sync(); //配置完成，开始绑定server，通过调用sync同步方法阻塞直到绑定成功
            channelFuture.channel().closeFuture().sync();//等待对应的host port 的管道关闭才会继续往下执行 否则一直阻塞
        }finally {
            eventLoopGroup.shutdownGracefully();
        }



    }


}
