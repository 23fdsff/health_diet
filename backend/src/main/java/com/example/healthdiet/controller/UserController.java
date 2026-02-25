package com.example.healthdiet.controller;

import com.example.healthdiet.common.ApiResponse;
import com.example.healthdiet.entity.DietRecord;
import com.example.healthdiet.entity.Recommendation;
import com.example.healthdiet.entity.UserProfile;
import com.example.healthdiet.service.DietRecordService;
import com.example.healthdiet.service.RecommendationService;
import com.example.healthdiet.service.UserProfileService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 普通用户相关接口。
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Resource
    private UserProfileService userProfileService;
    @Resource
    private RecommendationService recommendationService;
    @Resource
    private DietRecordService dietRecordService;

    // ======== 个人档案 ========

    @GetMapping("/profile")
    public ApiResponse<UserProfile> getProfile(@RequestParam Long userId) {
        return ApiResponse.success(userProfileService.getByUserId(userId));
    }

    @PostMapping("/profile")
    public ApiResponse<Void> saveProfile(@RequestBody UserProfile profile) {
        userProfileService.saveOrUpdate(profile);
        return ApiResponse.success(null);
    }

    // ======== 推荐 ========

    @GetMapping("/recommendations")
    public ApiResponse<List<Recommendation>> getRecommendations(@RequestParam Long userId,
                                                                @RequestParam String date) {
        return ApiResponse.success(
                recommendationService.listDailyRecommendations(userId, LocalDate.parse(date)));
    }

    @PostMapping("/recommendations/generate")
    public ApiResponse<List<Recommendation>> generateRecommendations(@RequestParam Long userId,
                                                                     @RequestParam String date) {
        return ApiResponse.success(
                recommendationService.generateDailyRecommendations(userId, LocalDate.parse(date)));
    }

    // ======== 饮食记录 ========

    @PostMapping("/dietRecord")
    public ApiResponse<Void> addDietRecord(@RequestBody DietRecord record) {
        dietRecordService.addRecord(record);
        return ApiResponse.success(null);
    }

    @GetMapping("/dietRecord/list")
    public ApiResponse<List<DietRecord>> listDietRecord(@RequestParam Long userId) {
        return ApiResponse.success(dietRecordService.listByUser(userId));
    }

    @GetMapping("/dietRecord/stat")
    public ApiResponse<Map<String, Object>> statDietRecord(@RequestParam Long userId) {
        return ApiResponse.success(dietRecordService.statByUser(userId));
    }
}

