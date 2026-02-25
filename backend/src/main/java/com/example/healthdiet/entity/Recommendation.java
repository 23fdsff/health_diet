package com.example.healthdiet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 推荐结果，对应表 recommendation。
 */
@Data
@TableName("recommendation")
public class Recommendation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private LocalDate recDate;

    private Integer planIndex;

    private Long breakfastId;

    private Long lunchId;

    private Long dinnerId;

    /**
     * 1规则匹配 2协同过滤 3混合
     */
    private Integer algorithmType;

    private Double score;

    private Integer status;

    private LocalDateTime createTime;
}

