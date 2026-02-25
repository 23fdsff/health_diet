package com.example.healthdiet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 饮食记录，对应表 diet_record。
 */
@Data
@TableName("diet_record")
public class DietRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private LocalDate recordDate;

    /**
     * 1早餐 2午餐 3晚餐 4加餐
     */
    private Integer mealType;

    private Long recipeId;

    private String description;

    private Double calories;

    private Integer satisfaction;

    private LocalDateTime createTime;
}

