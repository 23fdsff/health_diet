package com.example.healthdiet.service;

import com.example.healthdiet.entity.Recommendation;

import java.time.LocalDate;
import java.util.List;

/**
 * 推荐服务接口。
 */
public interface RecommendationService {

    /**
     * 为用户在指定日期生成三套推荐方案。
     */
    List<Recommendation> generateDailyRecommendations(Long userId, LocalDate date);

    /**
     * 查询当天推荐方案。
     */
    List<Recommendation> listDailyRecommendations(Long userId, LocalDate date);
}

