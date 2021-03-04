package com.manager.traffic.util;


import com.manager.traffic.exception.ErrorCodeEnum;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;


public class ResponseUtil {
    private ResponseUtil() {
    }

    private static final ResponseResult notFoundGeneralResponse = ResponseResult.failure(ErrorCodeEnum.USER_INFO_NOT_FOUND);

    public static void notFound(ChannelHandlerContext ctx, boolean keepAlive) {
        response(ctx, keepAlive, notFoundGeneralResponse);
    }

    /**
     * 响应HTTP的请求
     *
     * @param ctx
     * @param keepAlive
     * @param generalResponse
     */
    public static void response(ChannelHandlerContext ctx, boolean keepAlive, ResponseResult generalResponse) {
        byte[] jsonByteByte = JsonUtil.toJson(generalResponse).getBytes();
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(jsonByteByte));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.write(response);
        }
        ctx.flush();
    }
}
