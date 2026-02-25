package com.example.healthdiet.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户健康档案，对应表 user_profile。
 */
@Data
@TableName("user_profile")
public class UserProfile {

    @TableId
    private Long id;

    private Long userId;

    private Integer gender;

    private Integer age;

    private Double heightCm;

    private Double weightKg;

    private Double bmi;

    private String chronicDisease;

    private String allergy;

    private String tastePrefer;

    private String goal;

    private LocalDateTime updateTime;
}

