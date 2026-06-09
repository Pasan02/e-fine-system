package com.slpolice.trafficfines.auth.service;

import com.slpolice.trafficfines.auth.dto.LoginRequest;
import com.slpolice.trafficfines.auth.dto.LoginResponse;
import com.slpolice.trafficfines.auth.dto.RegisterRequest;
import com.slpolice.trafficfines.auth.entity.User;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    User register(RegisterRequest registerRequest);
}
