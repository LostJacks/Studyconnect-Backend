package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.AuthService;
import com.studyconnect.presentation.dto.AuthResponse;
import com.studyconnect.presentation.dto.LoginRequest;
import com.studyconnect.presentation.dto.RegisterRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}