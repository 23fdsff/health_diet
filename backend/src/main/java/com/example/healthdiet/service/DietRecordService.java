package com.example.healthdiet.service;

import com.example.healthdiet.entity.DietRecord;

import java.util.List;
import java.util.Map;

/**
 * 饮食记录服务。
 */
public interface DietRecordService {

    void addRecord(DietRecord record);

    /**
     * 查询某用户的饮食记录列表（最近若干条）。
     */
    List<DietRecord> listByUser(Long userId);

    /**
     * 简单统计：总记录数、近7天记录数。
     */
    Map<String, Object> statByUser(Long userId);
}

