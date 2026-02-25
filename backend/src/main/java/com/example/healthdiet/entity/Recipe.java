package com.example.healthdiet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 食谱实体，对应表 recipe。
 */
@Data
@TableName("recipe")
public class Recipe {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    /**
     * 1早餐 2午餐 3晚餐 4加餐
     */
    private Integer type;

    private Double totalCalories;

    private String suitableFor;

    private String avoidTags;

    private Integer difficulty;

    private Integer cookTimeMin;

    private String coverImg;

    private String description;

    private String steps;

    private Long createBy;
}

