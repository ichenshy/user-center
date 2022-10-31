package com.chen.user.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chenshy
 * @version v1.0
 * @date 2022/8/21
 */
@Data
public class PageRequest implements Serializable {
    private static final long serialVersionUID = -2983668862534836263L;
    /**
     * 当前页码
     */
    private long pageNum = 1;

    /**
     * 页面大小
     */
    private long pageSize = 5;

}
