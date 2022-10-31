package com.chen.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 * @author chenshy
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id

     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账号
     */
    @TableField(value = "user_account")
    private String userAccount;

    /**
     * 用户昵称

     */
    @TableField(value = "username")
    private String username;

    /**
     * 用户头像
     */
    @TableField(value = "avatar_url")
    private String avatarUrl;

    /**
     * 性别
     */
    @TableField(value = "gender")
    private Integer gender;

    /**
     * 密码
     */
    @TableField(value = "user_password")
    private String userPassword;

    /**
     * 标签
     */
    @TableField(value = "tags")
    private String tags;

    /**
     * 电话
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    private String email;
    /**
     * 简介
     */
    @TableField(value = "profile")
    private String profile;

    /**
     * 用户状态
     */
    @TableField(value = "user_status")
    private Integer userStatus;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 逻辑删除
     */
    @TableField(value = "is_delete")
    @TableLogic
    private Integer isDelete;

    /**
     * 角色 0普通用户 1 管理员
     */
    @TableField(value = "role")
    private Integer role;

    /**
     * 星球id
     */
    @TableField(value = "planet_code")
    private String planetCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
