package com.pricecompare.service;

import com.pricecompare.dto.request.LoginRequest;
import com.pricecompare.dto.response.LoginResponse;
import com.pricecompare.entity.User;

/**
 * 用户服务接口
 * 
 * @author AutoValuePilot
 */
public interface UserService {
    
    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 用户注册
     */
    void register(User user);
    
    /**
     * 根据ID查询用户
     */
    User getUserById(Long id);
    
    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(String username);
    
    /**
     * 更新用户信息
     */
    void updateUser(User user);
}