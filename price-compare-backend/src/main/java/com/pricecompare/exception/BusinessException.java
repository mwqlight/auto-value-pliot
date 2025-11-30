package com.pricecompare.exception;

/**
 * 业务异常类
 * 
 * @author AutoValuePilot
 */
public class BusinessException extends RuntimeException {
    
    private final Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
        this.code = 10000; // 默认业务错误码
    }

    public Integer getCode() {
        return code;
    }
}