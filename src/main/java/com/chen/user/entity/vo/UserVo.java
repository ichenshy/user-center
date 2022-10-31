package com.chen.user.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户
 *
 * @TableName user
 */
@Data
public class UserVo implements Serializable {
    private static final long serialVersionUID = 8101412230781707939L;
    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;


    /**
     * 标签
     */
    private String tags;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;
    /**
     * 简介
     */
    private String profile;


    /**
     * 角色 0普通用户 1 管理员
     */
    private Integer role;

    /**
     * 星球id
     */
    private String planetCode;

}
