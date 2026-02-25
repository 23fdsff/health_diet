package com.example.healthdiet.service.impl;

import com.example.healthdiet.entity.UserProfile;
import com.example.healthdiet.mapper.UserProfileMapper;
import com.example.healthdiet.service.UserProfileService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户档案服务实现。
 */
@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Resource
    private UserProfileMapper userProfileMapper;

    @Override
    public UserProfile getByUserId(Long userId) {
        return userProfileMapper.selectByUserId(userId);
    }

    @Override
    public void saveOrUpdate(UserProfile profile) {
        if (profile.getId() == null) {
            userProfileMapper.insert(profile);
        } else {
            userProfileMapper.updateById(profile);
        }
    }
}

