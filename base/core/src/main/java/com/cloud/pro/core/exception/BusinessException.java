package com.cloud.pro.core.exception;

import com.cloud.pro.core.response.ResponseCode;
import lombok.Data;

/**
 * 自定义业务异常类
 * @author han
 */
@Data
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private Integer code;
    /**
     * 错误信息
     */
    private String message;

    public BusinessException() {
        this.code = ResponseCode.ERROR.getCode();
        this.message = ResponseCode.ERROR.getDesc();
    }

    public BusinessException(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getDesc();
    }

    public BusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message) {
        this.message = message;
    }
}
