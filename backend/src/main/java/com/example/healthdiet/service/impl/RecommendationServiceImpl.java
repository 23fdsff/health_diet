package com.example.healthdiet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.healthdiet.entity.DietRecord;
import com.example.healthdiet.entity.Recipe;
import com.example.healthdiet.entity.Recommendation;
import com.example.healthdiet.entity.UserProfile;
import com.example.healthdiet.mapper.DietRecordMapper;
import com.example.healthdiet.mapper.RecipeMapper;
import com.example.healthdiet.mapper.RecommendationMapper;
import com.example.healthdiet.mapper.UserProfileMapper;
import com.example.healthdiet.service.RecommendationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐服务实现：规则匹配 + 简单协同过滤。
 */
@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Resource
    private UserProfileMapper userProfileMapper;
    @Resource
    private RecipeMapper recipeMapper;
    @Resource
    private DietRecordMapper dietRecordMapper;
    @Resource
    private RecommendationMapper recommendationMapper;

    @Override
    @Transactional
    public List<Recommendation> generateDailyRecommendations(Long userId, LocalDate date) {
        UserProfile profile = userProfileMapper.selectByUserId(userId);
        if (profile == null) {
            throw new RuntimeException("请先完善个人健康档案");
        }

        List<Recipe> all = recipeMapper.selectList(null);
        List<Recipe> ruleFiltered = applyRuleFilter(all, profile);
        Map<Long, Double> cfScores = calcCollaborativeScores(userId, ruleFiltered);
        List<RecipeScore> scored = mixScore(ruleFiltered, profile, cfScores);

        List<Recommendation> plans = buildPlans(userId, date, scored);
        recommendationMapper.deleteByUserAndDate(userId, date);
        for (Recommendation r : plans) {
            recommendationMapper.insert(r);
        }
        return plans;
    }

    @Override
    public List<Recommendation> listDailyRecommendations(Long userId, LocalDate date) {
        LambdaQueryWrapper<Recommendation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Recommendation::getUserId, userId)
                .eq(Recommendation::getRecDate, date)
                .orderByAsc(Recommendation::getPlanIndex);
        return recommendationMapper.selectList(wrapper);
    }

    private List<Recipe> applyRuleFilter(List<Recipe> all, UserProfile profile) {
        String allergy = Optional.ofNullable(profile.getAllergy()).orElse("");
        String disease = Optional.ofNullable(profile.getChronicDisease()).orElse("");
        String goal = Optional.ofNullable(profile.getGoal()).orElse("");

        return all.stream().filter(r -> {
            if (r.getAvoidTags() != null && !r.getAvoidTags().isEmpty()) {
                for (String tag : r.getAvoidTags().split(",")) {
                    if (!tag.trim().isEmpty() && disease.contains(tag.trim())) {
                        return false;
                    }
                }
            }
            if (!allergy.isEmpty() && r.getSuitableFor() != null &&
                    r.getSuitableFor().contains("海鲜") && allergy.contains("海鲜")) {
                return false;
            }
            if (!goal.isEmpty() && r.getSuitableFor() != null &&
                    !r.getSuitableFor().contains(goal)) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    private Map<Long, Double> calcCollaborativeScores(Long userId, List<Recipe> candidates) {
        if (candidates.isEmpty()) return Collections.emptyMap();

        // 所有有评分的记录
        LambdaQueryWrapper<DietRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(DietRecord::getRecipeId)
                .isNotNull(DietRecord::getSatisfaction);
        List<DietRecord> allRecords = dietRecordMapper.selectList(wrapper);

        // 按用户分组
        Map<Long, List<DietRecord>> byUser = allRecords.stream()
                .collect(Collectors.groupingBy(DietRecord::getUserId));

        List<DietRecord> currentUserRecords = byUser.getOrDefault(userId, Collections.emptyList());
        if (currentUserRecords.isEmpty()) {
            return Collections.emptyMap();
        }

        // 计算与其它用户的简单相似度：共同评价的食谱越多，评分差距越小，相似度越高
        Map<Long, Double> userSim = new HashMap<>();
        for (Map.Entry<Long, List<DietRecord>> entry : byUser.entrySet()) {
            Long otherId = entry.getKey();
            if (Objects.equals(otherId, userId)) continue;
            List<DietRecord> otherList = entry.getValue();
            double sim = calcUserSimilarity(currentUserRecords, otherList);
            if (sim > 0) {
                userSim.put(otherId, sim);
            }
        }

        // 对候选食谱打分：相似用户的加权平均满意度
        Map<Long, Double> result = new HashMap<>();
        Set<Long> candidateIds = candidates.stream()
                .map(Recipe::getId)
                .collect(Collectors.toSet());
        for (Long recipeId : candidateIds) {
            double sum = 0;
            double weight = 0;
            for (Map.Entry<Long, Double> e : userSim.entrySet()) {
                Long otherId = e.getKey();
                double w = e.getValue();
                List<DietRecord> list = byUser.get(otherId);
                if (list == null) continue;
                for (DietRecord dr : list) {
                    if (Objects.equals(dr.getRecipeId(), recipeId) &&
                            dr.getSatisfaction() != null) {
                        sum += w * dr.getSatisfaction();
                        weight += w;
                    }
                }
            }
            if (weight > 0) {
                result.put(recipeId, sum / weight);
            }
        }
        return result;
    }

    private double calcUserSimilarity(List<DietRecord> a, List<DietRecord> b) {
        Map<Long, Integer> mapA = a.stream()
                .filter(dr -> dr.getRecipeId() != null && dr.getSatisfaction() != null)
                .collect(Collectors.toMap(DietRecord::getRecipeId, DietRecord::getSatisfaction, (x, y) -> y));
        Map<Long, Integer> mapB = b.stream()
                .filter(dr -> dr.getRecipeId() != null && dr.getSatisfaction() != null)
                .collect(Collectors.toMap(DietRecord::getRecipeId, DietRecord::getSatisfaction, (x, y) -> y));
        int count = 0;
        double score = 0;
        for (Map.Entry<Long, Integer> e : mapA.entrySet()) {
            Integer sb = mapB.get(e.getKey());
            if (sb != null) {
                count++;
                score += 5 - Math.abs(e.getValue() - sb); // 差距越小越高
            }
        }
        if (count == 0) return 0;
        return score / count;
    }

    private List<RecipeScore> mixScore(List<Recipe> rules,
                                       UserProfile profile,
                                       Map<Long, Double> cfScores) {
        List<RecipeScore> res = new ArrayList<>();
        for (Recipe r : rules) {
            double ruleScore = 1.0;
            if ("减脂".equals(profile.getGoal()) && r.getTotalCalories() != null) {
                ruleScore += Math.max(0, (700 - r.getTotalCalories()) / 700.0);
            }
            double cfScore = cfScores.getOrDefault(r.getId(), 0.0) / 5.0;
            double finalScore = ruleScore * 0.6 + cfScore * 0.4;
            res.add(new RecipeScore(r, finalScore));
        }
        res.sort((x, y) -> Double.compare(y.score, x.score));
        return res;
    }

    private List<Recommendation> buildPlans(Long userId, LocalDate date,
                                            List<RecipeScore> scored) {
        Map<Integer, List<RecipeScore>> byType = scored.stream()
                .collect(Collectors.groupingBy(rs -> rs.recipe.getType()));
        List<RecipeScore> breakfasts = byType.getOrDefault(1, Collections.emptyList());
        List<RecipeScore> lunches = byType.getOrDefault(2, Collections.emptyList());
        List<RecipeScore> dinners = byType.getOrDefault(3, Collections.emptyList());

        List<Recommendation> list = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Recommendation r = new Recommendation();
            r.setUserId(userId);
            r.setRecDate(date);
            r.setPlanIndex(i);
            r.setBreakfastId(getIdByIndex(breakfasts, i - 1));
            r.setLunchId(getIdByIndex(lunches, i - 1));
            r.setDinnerId(getIdByIndex(dinners, i - 1));
            r.setAlgorithmType(3);
            r.setScore(100.0 - 5 * (i - 1));
            list.add(r);
        }
        return list;
    }

    private Long getIdByIndex(List<RecipeScore> list, int index) {
        if (list == null || list.size() <= index) return null;
        return list.get(index).recipe.getId();
    }

    private static class RecipeScore {
        Recipe recipe;
        double score;

        RecipeScore(Recipe r, double s) {
            this.recipe = r;
            this.score = s;
        }
    }
}

