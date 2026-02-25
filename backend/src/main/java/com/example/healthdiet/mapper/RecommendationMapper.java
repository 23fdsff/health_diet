package com.example.healthdiet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.healthdiet.entity.Recommendation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Delete;

import java.time.LocalDate;

/**
 * 推荐结果 Mapper。
 */
@Mapper
public interface RecommendationMapper extends BaseMapper<Recommendation> {

    @Delete("DELETE FROM recommendation WHERE user_id = #{userId} AND rec_date = #{recDate}")
    int deleteByUserAndDate(Long userId, LocalDate recDate);
}

