package com.example.healthdiet.controller;

import com.example.healthdiet.common.ApiResponse;
import com.example.healthdiet.service.AuthService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 登录相关接口。
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Resource
    private AuthService authService;

    /**
     * 微信小程序登录：前端传 jsCode。
     */
    @PostMapping("/wxLogin")
    public ApiResponse<?> wxLogin(@RequestParam String jsCode) {
        return ApiResponse.success(authService.wxLogin(jsCode));
    }

    /**
     * 账号密码登录。
     * 这里同时支持 GET / POST，方便在浏览器地址栏直接测试。
     */
    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResponse<?> login(@RequestParam String username,
                                @RequestParam String password) {
        return ApiResponse.success(authService.login(username, password));
    }
}

