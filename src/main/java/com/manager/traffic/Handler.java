package com.manager.traffic;

import com.manager.traffic.Websocket.ChannelSupervise;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.log4j.Logger;
import com.manager.traffic.util.ThreadPool;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class Handler extends ChannelInboundHandlerAdapter {
    public static final String queue_name = "my_queue";
    public static final boolean autoAck = false;
    public static final boolean durable = true;
    private static Logger logger = Logger.getLogger(Handler.class);
    private WebSocketServerHandshaker handshaker;
    public static ChannelGroup users =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //http://crysislinux.github.io/smart_websocket_client/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object o) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        String json = "";
        logger.info("===================进入=======================");
        if (o instanceof FullHttpRequest) {
            logger.info("http");
            json = handleHttpRequest(ctx, (FullHttpRequest) o);
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(json.getBytes(StandardCharsets.UTF_8)));
            response.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            response.headers().set(ACCESS_CONTROL_ALLOW_HEADERS, "Origin, X-Requested-With, Content-Type, Accept");
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
            ctx.writeAndFlush(response);
        } else if (o instanceof WebSocketFrame) {
            logger.info("websocket");
            handleWebSocketFrame(ctx, (TextWebSocketFrame) o);
            // judgement_action(((TextWebSocketFrame) o).text(),ctx.channel());
        }
        logger.info("返回数据" + json);
    }

    /**
     * 处理业务流程 http://localhost:6789/index?id=33
     */
    private String handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest fuHr) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        String url = fuHr.uri();
        System.out.println("method:" + fuHr.method());
        String json;
        /**
         * 唯一的一次http请求，用于创建websocket
         * */
        //要求Upgrade为websocket，过滤掉get/Post
        if (!fuHr.decoderResult().isSuccess()
                || (!"websocket".equals(fuHr.headers().get("Upgrade")))) {
            if (fuHr.method().toString().equals("GET")) {
                if (url.contains("?")) {
                    url = url.split("\\?")[0];
                }
            }
            logger.info("URL: " + url);
            ByteBuf byteBuf = fuHr.content();
            String data = byteBuf.toString(Charset.forName("utf-8"));
            logger.info("data " + data);
            String className = "com.manager.traffic.Controller." + url.split("/")[1];
            String methodName = "Return";
            Class clz = Class.forName(className);
            Object object = clz.newInstance();
            Method m = object.getClass().getDeclaredMethod(methodName, String.class);
            json = (String) m.invoke(object, data);
            System.out.println(json);
            return json;

        }

        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:6789/websocket", null, false);
        handshaker = wsFactory.newHandshaker(fuHr);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory
                    .sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), fuHr);
        }
        return "";
    }

    /**
     * 拒绝不合法的请求，并返回错误信息
     */


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //添加连接
        logger.debug("客户端加入连接：" + ctx.channel());
        ChannelSupervise.addChannel(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //断开连接
        logger.debug("客户端断开连接：" + ctx.channel());
        ChannelSupervise.removeChannel(ctx.channel());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handleWebSocketFrame(final ChannelHandlerContext ctx, WebSocketFrame frame) throws IOException {
        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(
                    new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 本例程仅支持文本消息，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            logger.debug("本例程仅支持文本消息，不支持二进制消息");
            throw new UnsupportedOperationException(String.format(
                    "%s frame types not supported", frame.getClass().getName()));
        }
        // 返回应答消息
        final String request = ((TextWebSocketFrame) frame).text();
        logger.debug("服务端收到：" + request);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
//		factory.setVirtualHost("my_mq");
//		factory.setUsername("aaa");
//		factory.setPassword("bbb");
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.queueDeclare(queue_name, durable, false, false, null);
        System.out.println("Wait for message");
        // 消息分发处理
        channel.basicQos(1);
        final QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queue_name, autoAck, consumer);
        ThreadPool threadPool = new ThreadPool();
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        QueueingConsumer.Delivery deliver = consumer.nextDelivery();
                        String message = new String(deliver.getBody());
                        TextWebSocketFrame tws = new TextWebSocketFrame(message
                                + ctx.channel().id() + "：" + request);
                        // 群发
                        //ChannelSupervise.send2All(tws);
                        // 返回【谁发的发给谁】
                        ctx.channel().writeAndFlush(tws);
                        channel.basicAck(deliver.getEnvelope().getDeliveryTag(), false);
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }


    private static void sendHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        // 如果是非Keep-Alive，关闭连接
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

}
