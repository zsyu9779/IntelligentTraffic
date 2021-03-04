package com.manager.traffic;


import com.manager.traffic.exception.ErrorCodeEnum;
import com.manager.traffic.pojo.Action;
import com.manager.traffic.pojo.HttpLabel;
import com.manager.traffic.pojo.ResponseResult;
import com.manager.traffic.util.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


/**
 * @author 李文浩
 * @date 2018/9/5
 */
@Slf4j
public class RouterHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final String DELIMITER = "?";

    HttpRouter httpRouter;

    public RouterHandler(HttpRouter httpRouter) {
        this.httpRouter = httpRouter;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        ResponseResult responseResult;
        if (uri.contains(DELIMITER)) {
            uri = uri.substring(0, uri.indexOf(DELIMITER));
        }
        //根据不同的请求API做不同的处理(路由分发)
        Action action = httpRouter.getRoute(new HttpLabel(uri, request.method()));
        if (action != null) {
            String s = request.uri();
            if (request.headers().get(HttpHeaderNames.CONTENT_TYPE.toString()).equals(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())) {
                s = s + "&" + request.content().toString(StandardCharsets.UTF_8);
            }
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(s);
            Map<String, List<String>> parameters = queryStringDecoder.parameters();
            Class[] classes = action.getMethod().getParameterTypes();
            Object[] objects = new Object[classes.length];
            for (int i = 0; i < classes.length; i++) {
                Class c = classes[i];
                //处理@RequestBody注解
                Annotation[] parameterAnnotation = action.getMethod().getParameterAnnotations()[i];
                if (parameterAnnotation.length > 0) {
                    for (int j = 0; j < parameterAnnotation.length; j++) {
                        if (parameterAnnotation[j].annotationType() == RequestBody.class &&
                                request.headers().get(HttpHeaderNames.CONTENT_TYPE.toString()).equals(HttpHeaderValues.APPLICATION_JSON.toString())) {
                            objects[i] = JsonUtil.fromJson(request, c);
                        }
                    }
                    //处理数组类型
                } else if (c.isArray()) {
                    String paramName = action.getMethod().getParameters()[i].getName();
                    List<String> paramList = parameters.get(paramName);
                    if (CollectionUtils.isNotEmpty(paramList)) {
                        objects[i] = ParamParser.INSTANCE.parseArray(c.getComponentType(), paramList);
                    }
                } else {
                    //处理基本类型和string
                    String paramName = action.getMethod().getParameters()[i].getName();
                    List<String> paramList = parameters.get(paramName);
                    if (CollectionUtils.isNotEmpty(paramList)) {
                        objects[i] = ParamParser.INSTANCE.parseValue(c, paramList.get(0));
                    } else {
                        objects[i] = ParamParser.INSTANCE.parseValue(c, null);
                    }
                }
            }
            ResponseUtil.response(ctx, HttpUtil.isKeepAlive(request), action.call(objects));
        } else {
            //错误处理
            responseResult = ResponseResult.failure(ErrorCodeEnum.BAD_REQUEST.getCode(), "请检查你的请求方法及url");
            ResponseUtil.response(ctx, HttpUtil.isKeepAlive(request), responseResult);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        log.warn("{}", e);
        ResponseUtil.response(ctx, false, ResponseResult.failure(ErrorCodeEnum.UNKNOWN_ERROR.getCode(), String.format("Internal Error: %s", ExceptionUtils.getRootCause(e))));
        ctx.close();
    }
}
