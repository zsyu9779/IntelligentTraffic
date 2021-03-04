package com.manager.traffic.util;

import com.manager.traffic.exception.ErrorCodeEnum;
import lombok.Data;

@Data
public class MyRuntimeException extends RuntimeException {

    private ResponseResult generalResponse;

    public MyRuntimeException(String message) {
        generalResponse = ResponseResult.failure(ErrorCodeEnum.SYSTEM_ERROR.getCode(), message);
    }

    public MyRuntimeException(ErrorCodeEnum status, String message) {
        generalResponse =ResponseResult.failure(status.getCode(), message);
    }

    public MyRuntimeException(ResponseResult generalResponse) {
        this.generalResponse = generalResponse;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}