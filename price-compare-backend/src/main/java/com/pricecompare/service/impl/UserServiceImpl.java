package com.pricecompare.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pricecompare.dto.request.LoginRequest;
import com.pricecompare.dto.response.LoginResponse;
import com.pricecompare.entity.User;
import com.pricecompare.repository.UserRepository;
import com.pricecompare.service.UserService;
import com.pricecompare.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 * 
 * @author AutoValuePilot
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Override
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        User user = userRepository.selectByUsername(request.getUsername());
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }
        
        // 生成JWT令牌
        String token = jwtUtil.generateToken(user);
        
        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setAccessToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtUtil.getExpiration());
        
        log.info("用户登录成功: {}", user.getUsername());
        return response;
    }
    
    @Override
    public void register(User user) {
        // 检查用户名是否已存在
        User existingUser = userRepository.selectByUsername(user.getUsername());
        
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置默认值
        if (StrUtil.isBlank(user.getNickname())) {
            user.setNickname(user.getUsername());
        }
        user.setStatus(1);
        
        // 保存用户
        userRepository.insert(user);
        log.info("用户注册成功: {}", user.getUsername());
    }
    
    @Override
    public User getUserById(Long id) {
        return userRepository.selectById(id);
    }
    
    @Override
    public User getUserByUsername(String username) {
        return userRepository.selectByUsername(username);
    }
    
    @Override
    public void updateUser(User user) {
        if (StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.updateById(user);
        log.info("用户信息更新成功: {}", user.getUsername());
    }
}