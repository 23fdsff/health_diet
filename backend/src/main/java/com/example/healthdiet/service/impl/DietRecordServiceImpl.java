package com.example.healthdiet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.healthdiet.entity.DietRecord;
import com.example.healthdiet.mapper.DietRecordMapper;
import com.example.healthdiet.service.DietRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 饮食记录服务实现。
 */
@Service
public class DietRecordServiceImpl implements DietRecordService {

    @Resource
    private DietRecordMapper dietRecordMapper;

    @Override
    public void addRecord(DietRecord record) {
        if (record.getRecordDate() == null) {
            record.setRecordDate(LocalDate.now());
        }
        dietRecordMapper.insert(record);
    }

    @Override
    public List<DietRecord> listByUser(Long userId) {
        LambdaQueryWrapper<DietRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DietRecord::getUserId, userId)
                .orderByDesc(DietRecord::getRecordDate)
                .orderByDesc(DietRecord::getCreateTime)
                .last("LIMIT 100");
        return dietRecordMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> statByUser(Long userId) {
        Map<String, Object> map = new HashMap<>();
        LambdaQueryWrapper<DietRecord> all = new LambdaQueryWrapper<>();
        all.eq(DietRecord::getUserId, userId);
        long total = dietRecordMapper.selectCount(all);

        LambdaQueryWrapper<DietRecord> last7 = new LambdaQueryWrapper<>();
        last7.eq(DietRecord::getUserId, userId)
                .ge(DietRecord::getRecordDate, LocalDate.now().minusDays(7));
        long last7Count = dietRecordMapper.selectCount(last7);

        map.put("total", total);
        map.put("last7Days", last7Count);
        return map;
    }
}

