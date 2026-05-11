package com.antrian.app.service;

import com.antrian.app.dto.request.LoginRequest;
import com.antrian.app.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
