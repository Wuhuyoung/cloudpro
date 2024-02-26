package com.cloud.pro.core.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

/**
 * 公用返回对象
 * @author han
 * @param <T>
 */
// 当一个对象的属性值为 null 时，该属性不会序列化到生成的 JSON 中
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class Result<T> implements Serializable {
    private Integer code;
    private String message;
    private T data;

    private Result(Integer code) {
        this.code = code;
    }

    private Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 该属性在序列化为 JSON 时会被忽略
    @JsonIgnore
    @JSONField(serialize = false)
    public boolean isSuccess() {
        return Objects.equals(this.code, ResponseCode.SUCCESS.getCode());
    }

    public static <T> Result<T> success() {
        return new Result<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> Result<T> success(String message) {
        return new Result<T>(ResponseCode.SUCCESS.getCode(), message);
    }

    public static <T> Result<T> data(T data) {
        return new Result<T>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getDesc(), data);
    }

    public static <T> Result<T> fail() {
        return new Result<T>(ResponseCode.ERROR.getCode());
    }
    public static <T> Result<T> fail(String message) {
        return new Result<T>(ResponseCode.ERROR.getCode(), message);
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<T>(code, message);
    }

    public static <T> Result<T> fail(ResponseCode responseCode) {
        return new Result<T>(responseCode.getCode(), responseCode.getDesc());
    }
}
