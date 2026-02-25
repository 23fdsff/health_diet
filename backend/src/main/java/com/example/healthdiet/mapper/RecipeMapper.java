package com.example.healthdiet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.healthdiet.entity.Recipe;
import org.apache.ibatis.annotations.Mapper;

/**
 * 食谱 Mapper。
 */
@Mapper
public interface RecipeMapper extends BaseMapper<Recipe> {
}

