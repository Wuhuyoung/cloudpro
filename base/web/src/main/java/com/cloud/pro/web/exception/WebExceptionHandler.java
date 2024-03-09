package com.cloud.pro.web.exception;

import com.cloud.pro.core.exception.BusinessException;
import com.cloud.pro.core.exception.FrameworkException;
import com.cloud.pro.core.response.ResponseCode;
import com.cloud.pro.core.response.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class WebExceptionHandler {
    @ExceptionHandler(value = BusinessException.class)
    public Result businessExceptionHandler(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        ObjectError objectError = e.getBindingResult().getAllErrors().stream().findFirst().get();
        return Result.fail(ResponseCode.ERROR_PARAM.getCode(), objectError.getDefaultMessage());
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public Result constraintViolationExceptionHandler(ConstraintViolationException e) {
        ConstraintViolation<?> constraintViolation = e.getConstraintViolations().stream().findFirst().get();
        return Result.fail(ResponseCode.ERROR_PARAM.getCode(), constraintViolation.getMessage());
    }

    // 请求缺少必需参数
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public Result missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        return Result.fail(ResponseCode.ERROR_PARAM);
    }

    @ExceptionHandler(value = IllegalStateException.class)
    public Result illegalStateExceptionHandler(IllegalStateException e) {
        return Result.fail(ResponseCode.ERROR_PARAM);
    }

    @ExceptionHandler(value = BindException.class)
    public Result bindExceptionHandler(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldErrors().stream().findFirst().get();
        return Result.fail(ResponseCode.ERROR_PARAM.getCode(), fieldError.getDefaultMessage());
    }

    @ExceptionHandler(value = FrameworkException.class)
    public Result frameworkExceptionHandler(FrameworkException e) {
        return Result.fail(ResponseCode.ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = RuntimeException.class)
    public Result runtimeExceptionHandler(RuntimeException e) {
        return Result.fail(ResponseCode.ERROR.getCode(), e.getMessage());
    }
}
