package com.example.healthdiet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.healthdiet.entity.DietRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 饮食记录 Mapper。
 */
@Mapper
public interface DietRecordMapper extends BaseMapper<DietRecord> {
}

