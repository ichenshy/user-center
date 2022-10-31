package com.chen.user.entity.request;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍表
 * @TableName team
 * @author  chenshy
 */
@Data
public class TeamAddRequest implements Serializable {

    /**
     * 队伍名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 队伍描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 最大人数
     */
    @TableField(value = "max_num")
    private Integer maxNum;

    /**
     * 过期时间
     */
    @TableField(value = "expire_time")
    private Date expireTime;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 0-公开 1-私有 2-加密
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 房间密码
     */
    @TableField(value = "password")
    private String password;

}
