package com.example.healthdiet.service;

import com.example.healthdiet.entity.UserProfile;

/**
 * 用户档案服务。
 */
public interface UserProfileService {

    UserProfile getByUserId(Long userId);

    void saveOrUpdate(UserProfile profile);
}

