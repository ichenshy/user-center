package com.chen.user.common;

/**
 * 返回工具类
 * @author Galaxy
 * @version v1.0
 * @date 2022/7/26
 */
public class ResultUtils {
    /**
     * 成功
     *
     * @param data 数据
     * @return {@code BaseResponse<T>}
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }
    /**
     * 成功
     *
     * @return {@code BaseResponse<T>}
     */
    public static <T> BaseResponse<T> success() {
        return new BaseResponse(0, "", "ok");
    }
    /**
     * 错误
     *
     * @param errorCode 错误代码
     * @return {@code BaseResponse}
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse(errorCode);
    }
    /**
     * 错误
     *
     * @param errorCode   错误代码
     * @param message     消息
     * @param description 描述
     * @return {@code BaseResponse}
     */
    public static BaseResponse error(ErrorCode errorCode, String message, String description) {
        return new BaseResponse(errorCode.getCode(), message, description);
    }
    /**
     * 错误
     *
     * @param errorCode   错误代码
     * @param description 描述
     * @return {@code BaseResponse}
     */
    public static BaseResponse error(ErrorCode errorCode, String description) {
        return new BaseResponse(errorCode.getCode(), errorCode.getMessage(), description);
    }
    /**
     * 错误
     *
     * @param code        代码
     * @param message     消息
     * @param description 描述
     * @return {@code BaseResponse}
     */
    public static BaseResponse error(int code, String message, String description) {
        return new BaseResponse(code, null, message, description);
    }
}
