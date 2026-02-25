package com.example.healthdiet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.healthdiet.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户健康档案 Mapper。
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {

    /**
     * 根据 user_id 查询档案。
     */
    @Select("SELECT * FROM user_profile WHERE user_id = #{userId} LIMIT 1")
    UserProfile selectByUserId(Long userId);
}

