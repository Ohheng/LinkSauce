package com.ohh.linksauceapibackend.exception;

import com.ohh.linksauceapibackend.common.ErrorCode;

/**
 * @Author: Ohh
 * @Desctription: 自定义异常类
 * @Date: 2024-05-09 16:16
 * @Version: 1.0
 */
public class BusinessException extends RuntimeException{

    private static final long serialVersionUID = 2942420535500634982L;
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
