package com.chen.user.entity.request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍表
 * @author  chenshy
 */
@Data
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = 7621969260944964128L;
    /**
     *id
     */

    private Long id;

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
     * 过期时间
     */
    @TableField(value = "expire_time")
    private Date expireTime;

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
