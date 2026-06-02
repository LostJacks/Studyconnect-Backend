package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.IAuthService;
import com.studyconnect.presentation.dto.AuthResponse;
import com.studyconnect.presentation.dto.LoginRequest;
import com.studyconnect.presentation.dto.RegisterRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private final IAuthService authService =
            mock(IAuthService.class);

    private final AuthController authController =
            new AuthController(authService);

    @Test
    void register_returnsAuthResponse() {
        RegisterRequest request = new RegisterRequest();

        AuthResponse response = mock(AuthResponse.class);

        when(authService.register(request))
                .thenReturn(response);

        AuthResponse result =
                authController.register(request);

        assertSame(response, result);

        verify(authService).register(request);
    }

    @Test
    void login_returnsAuthResponse() {
        LoginRequest request = new LoginRequest();

        AuthResponse response = mock(AuthResponse.class);

        when(authService.login(request))
                .thenReturn(response);

        AuthResponse result =
                authController.login(request);

        assertSame(response, result);

        verify(authService).login(request);
    }
}