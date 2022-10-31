package com.chen.user.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 基反应
 *
 * @author chenshy
 * @date 2022/08/30
 */
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = -927256797531745017L;
    private int code;
    private T data;
    private String message;
    private String description;
    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this.code = code;
        this.data = data;
        this.message = "";
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }
}
