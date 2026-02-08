package com.hbnu.zen.config;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.BusinessException;
import com.hbnu.zen.common.ErrorCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException ex) {
        return ApiResponse.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidation(MethodArgumentNotValidException ex) {
        FieldError error = ex.getBindingResult().getFieldError();
        String message = error == null ? "参数错误" : error.getField() + ": " + error.getDefaultMessage();
        return ApiResponse.error(ErrorCode.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Void> handleBody(HttpMessageNotReadableException ex) {
        return ApiResponse.error(ErrorCode.BAD_REQUEST, "请求体解析失败");
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleOther(Exception ex) {
        return ApiResponse.error(ErrorCode.SERVER_ERROR, "服务器内部错误");
    }
}
