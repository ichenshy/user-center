package com.chen.user.common;

/**
 * 错误代码
 *
 * @author Galaxy
 * @version v1.0
 * @date 2022/7/26
 * 错误码
 */
public enum ErrorCode {
    /** 成功 */ SUCCESS(0,"ok",""),
    /** 参数错误 */ PARAMS_ERROR(40000,"请求参数错误",""),
    /** 请求数据为空 */ NULL_ERROR(40001,"请求数据为空",""),
    /** 没有身份验证 */ NOT_AUTH(40100,"无权限",""),
    /** 未登录 */ NOT_LOGIN(40100,"未登录",""),
    /** 禁止操作 */ FORBIDDEN(40100,"禁止操作",""),
    /** 系统错误 */ SYSTEM_ERROR(50000,"系统内部异常","");


    /** 代码 */
    private final int code;
    /** 消息 */
    private final String message;
    /** 描述 */
    private final String description;

    /**
     * 错误代码
     *
     * @param code        代码
     * @param message     消息
     * @param description 描述
     */
    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    /**
     * 获取代码
     *
     * @return int
     */
    public int getCode() {
        return code;
    }

    /**
     * 得到消息
     *
     * @return {@code String}
     */
    public String getMessage() {
        return message;
    }

    /**
     * 得到描述
     *
     * @return {@code String}
     */
    public String getDescription() {
        return description;
    }
}
