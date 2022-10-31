package com.chen.user.entity.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 团队辞职请求
 *
 * @author chenshy
 * @date 2022/09/05
 */
@Data
public class TeamQuitRequest implements Serializable {
    private static final long serialVersionUID = 8798474121494722704L;
    private Long teamId;

}
