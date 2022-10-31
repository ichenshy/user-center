package com.chen.user.entity.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 队伍表
 *
 * @author chenshy
 * @TableName team
 */
@Data
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = -7439795250170262443L;

    private Long teamId;

    private String password;

}
