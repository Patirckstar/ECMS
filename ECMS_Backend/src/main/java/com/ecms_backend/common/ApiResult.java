package com.ecms_backend.common;

import lombok.Data;

/**
 * 统一 API 响应格式
 */
@Data
public class ApiResult<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> result = new ApiResult<>();
        result.code = 200;
        result.message = "success";
        result.data = data;
        return result;
    }

    public static <T> ApiResult<T> success(String message, T data) {
        ApiResult<T> result = new ApiResult<>();
        result.code = 200;
        result.message = message;
        result.data = data;
        return result;
    }

    public static <T> ApiResult<T> error(int code, String message) {
        ApiResult<T> result = new ApiResult<>();
        result.code = code;
        result.message = message;
        result.data = null;
        return result;
    }
}
