package com.manager.traffic;

import com.manager.traffic.util.ThreadPool;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

 public class NettyServer {

     private void start() {
         //接收组，处理来访问服务器的客户的连接请求 IO线程池
         EventLoopGroup bossGroup = new NioEventLoopGroup(0x1, ThreadPool.pool);
         //工作组，实现数据的读写 业务线程池
         EventLoopGroup workGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 0X3, ThreadPool.pool);

         try {
             //服务端来设置通道参数的工具
             ServerBootstrap bootstrap;
             bootstrap = new ServerBootstrap();
             //将两个工作线程与通道绑定
             bootstrap.group(bossGroup, workGroup)
                     //指定NIO模式
                     .channel(NioServerSocketChannel.class)
                     .childHandler(new ChannelInitializer<SocketChannel>() {

                         @Override
                         protected void initChannel(SocketChannel socketChannel) throws Exception {//设定回调类
                             socketChannel.pipeline().addLast(new HttpResponseEncoder());//server端发送的是httpResponse,要进行编码

                             socketChannel.pipeline().addLast(new HttpRequestDecoder());//server端接收的是httpRequest,要进行解码

                             socketChannel.pipeline().addLast(new HttpObjectAggregator(65535));
                             //等待解码后的报文头和报文体一起扔给下一层
                             socketChannel.pipeline().addLast(new ChunkedWriteHandler());
                            // socketChannel.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));
                             socketChannel.pipeline().addLast(new Handler());//自定义handler
                             //        socketChannel.pipeline().addLast(new WebSocket());

                         }


                     })//设置回调
                     //设置TCP缓冲区
                     .option(ChannelOption.SO_BACKLOG, 128)
                     //设置长连接
                     .childOption(ChannelOption.SO_KEEPALIVE, true);
             //绑定端口
             ChannelFuture future = bootstrap.bind("127.0.0.1", 6789).sync();
             //阻止程序关闭
             future.channel().closeFuture().sync();


         } catch (InterruptedException e) {
             e.printStackTrace();
         }finally {
             bossGroup.shutdownGracefully();
             workGroup.shutdownGracefully();
         }

     }

     public static void main(String[] args) {
         NettyServer nettyServer = new NettyServer();
         //EsUtil.getClient();
         nettyServer.start();

     }
 }
