package com.example.healthdiet.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 简化版“令牌”工具类。
 *
 * 为了避免 JDK 版本与 jjwt 依赖冲突，这里不再真正生成 JWT，
 * 而是返回一个随机字符串作为 token，仅用于前后端演示。
 * 当前项目也没有做基于 token 的权限校验，因此足够使用。
 */
@Component
public class JwtUtil {

    /**
     * 生成一个随机 token 字符串。
     */
    public String generateToken(Long userId, String roleCodes) {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

