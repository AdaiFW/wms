package com.warehouse.service;

import com.warehouse.dto.LoginDto;
import com.warehouse.dto.RegisterDto;

import java.util.Map;

public interface AuthService {
    Map<String, Object> login(LoginDto dto);
    void register(RegisterDto dto);
    Map<String, Object> refreshToken(String refreshToken);
    void logout(String token);
}
