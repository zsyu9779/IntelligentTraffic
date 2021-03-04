package com.manager.traffic.util;

import com.manager.traffic.exception.ErrorCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 类名称：ResponseResult
 * ********************************
 * <p>
 * 类描述：通用返回结果模型
 *
 * @author
 * @date 下午12:59
 */
@Data
public class ResponseResult<T> implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7813356989387725160L;

    private Message message;

    private T result;

    /**
     * 成功
     * @param result
     * @param <T>
     * @return
     */
    public static <T> ResponseResult<T> success(T result) {
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setMessage(new Message(ErrorCodeEnum.SUCCESS.getCode(),
                ErrorCodeEnum.SUCCESS.getMessage(),
                System.currentTimeMillis()));
        responseResult.setResult(result);

        return responseResult;
    }

    /**
     * 失败
     * @param code
     * @param message
     * @param <T>
     * @return
     */
    public static <T> ResponseResult<T> failure(int code, String message) {
        ResponseResult<T> responseResult = new ResponseResult<>();

        responseResult.setMessage(new Message(code, message, System.currentTimeMillis()));

        return responseResult;
    }

    /**
     * 失败
     * @param codeEnum
     * @param <T>
     * @return
     */
    public static <T> ResponseResult<T> failure(ErrorCodeEnum codeEnum) {
        return failure(codeEnum.getCode(), codeEnum.getMessage());
    }

    @AllArgsConstructor
    @Data
    static class Message implements Serializable {
        private static final long serialVersionUID = 1190706416089418783L;

        private int code;
        private String messageInfo;
        private long serverTime;
    }
}
