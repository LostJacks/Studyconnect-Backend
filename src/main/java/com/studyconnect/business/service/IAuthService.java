package com.studyconnect.business.service;

import com.studyconnect.presentation.dto.AuthResponse;
import com.studyconnect.presentation.dto.LoginRequest;
import com.studyconnect.presentation.dto.RegisterRequest;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}