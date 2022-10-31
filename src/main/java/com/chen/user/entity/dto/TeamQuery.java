package com.chen.user.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chen.user.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 队伍查询封装类
 *
 * @author chenshy
 * @TableName TeamQuery
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TeamQuery extends PageRequest implements Serializable {
    private static final long serialVersionUID = 8233370734183572020L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * id 列表
     */
    private List<Long> idList;

    /**
     * 队伍名称
     */
    @TableField(value = "name")
    private String name;
    /**
     * 名称和描述搜索词
     */
    private String SearchText;

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
     * 当前页码
     */
    private long pageNum;
    /**
     * 页面大小
     */
    private long pageSize;


}
