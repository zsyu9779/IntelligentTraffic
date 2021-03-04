package com.manager.traffic.pojo;

import com.manager.traffic.util.MyRuntimeException;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@Data
@RequiredArgsConstructor
@Slf4j
public class Action {
    @NonNull
    private Object object;
    @NonNull
    private Method method;

    private List<Class> paramsClassList;

    public GeneralResponse call(Object... args) {
        try {
            return (GeneralResponse) method.invoke(object, args);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            //实现 `@ControllerAdvice` 异常处理，直接抛出自定义异常
            if (targetException instanceof MyRuntimeException) {
                return ((MyRuntimeException) targetException).getGeneralResponse();
            }
            log.warn("method invoke error: {}", e);
            return new GeneralResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, String.format("Internal Error: %s", ExceptionUtils.getRootCause(e)), null);
        } catch (IllegalAccessException e) {
            log.warn("method invoke error: {}", e);
            return new GeneralResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, String.format("Internal Error: %s", ExceptionUtils.getRootCause(e)), null);
        }
    }
}
