package com.example.healthdiet.service.impl;

import com.example.healthdiet.service.AuthService;
import com.example.healthdiet.util.JwtUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录实现（演示版本，未接数据库）。
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public Map<String, Object> wxLogin(String jsCode) {
        // 真实项目应通过 jsCode 调用微信接口换取 openId
        // 此处为了演示，直接模拟一个普通用户
        Long userId = 1L;
        String roles = "ROLE_USER";
        String token = jwtUtil.generateToken(userId, roles);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("nickName", "微信用户");
        result.put("roleCodes", new String[]{"ROLE_USER"});
        result.put("token", token);
        return result;
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        // 示例：根据用户名简单判定角色
        String role;
        if ("admin".equals(username)) {
            role = "ROLE_ADMIN";
        } else if ("dietitian".equals(username)) {
            role = "ROLE_DIETITIAN";
        } else if ("ent".equals(username)) {
            role = "ROLE_ENTERPRISE";
        } else {
            role = "ROLE_USER";
        }
        Long userId = 2L;
        String token = jwtUtil.generateToken(userId, role);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("nickName", username);
        result.put("roleCodes", new String[]{role});
        result.put("token", token);
        return result;
    }
}

