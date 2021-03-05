package com.manager.traffic.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;

import java.lang.reflect.Type;

public class JsonUtil {

    private JsonUtil() {

    }

    private static final GsonBuilder GSON_BUILDER = new GsonBuilder();

    private static final Gson GSON = GSON_BUILDER.disableHtmlEscaping().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public static String toJson(Object o) {
        return GSON.toJson(o);
    }

    public static <T> T fromJson(FullHttpRequest request, Class<T> c) {
        ByteBuf jsonBuf = request.content();
        String jsonStr = jsonBuf.toString(CharsetUtil.UTF_8);
        return GSON.fromJson(jsonStr, c);
    }

    public static <T> T fromJson(String json, Class<T> c) {
        return GSON.fromJson(json, c);
    }

    /**
     * 把json字符串解析成List：
     * params: new TypeToken<List<yourbean>>(){}.getType()
     * 把json字符串解析成map：
     * new TypeToken<HashMap<String,Object>>() {}.getType()
     *
     * @param request
     * @param type    new TypeToken<List<yourbean>>(){}.getType()
     * @return
     */
    public static <T> T fromJson(FullHttpRequest request, Type type) {
        ByteBuf jsonBuf = request.content();
        String json = jsonBuf.toString(CharsetUtil.UTF_8);
        return GSON.fromJson(json, type);
    }
}
