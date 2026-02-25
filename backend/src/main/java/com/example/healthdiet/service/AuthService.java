package com.example.healthdiet.service;

import java.util.Map;

/**
 * 登录相关业务接口。
 */
public interface AuthService {

    /**
     * 微信登录：根据 jsCode 获取 openId，返回 token 和角色信息。
     * 这里为了示例，直接伪造 openId 和用户。
     */
    Map<String, Object> wxLogin(String jsCode);

    /**
     * 账号密码登录，返回 token 和角色信息。
     * 示例中直接做简单判断，真实项目需要从数据库校验。
     */
    Map<String, Object> login(String username, String password);
}

