package com.manager.traffic.util;

import com.manager.traffic.exception.ErrorCodeEnum;
import com.manager.traffic.pojo.ResponseResult;
import lombok.Data;

@Data
public class MyRuntimeException extends RuntimeException {


    public MyRuntimeException(String message) {
        ResponseResult.failure(ErrorCodeEnum.SYSTEM_ERROR.getCode(), message);
    }

    public MyRuntimeException(ErrorCodeEnum status, String message) {
        ResponseResult.failure(status.getCode(), message);
    }


    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}