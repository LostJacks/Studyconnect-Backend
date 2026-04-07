package com.studyconnect.business.service;

import com.studyconnect.data.entity.UserEntity;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.domain.model.Role;
import com.studyconnect.domain.model.User;
import com.studyconnect.presentation.dto.AuthResponse;
import com.studyconnect.presentation.dto.LoginRequest;
import com.studyconnect.presentation.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private IUserRepository userRepository;
    private JwtService jwtService;
    private AuthService authService;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository = mock(IUserRepository.class);
        jwtService = mock(JwtService.class);
        authService = new AuthService(userRepository, jwtService);
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void register_validRequest_returnsAuthResponse() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Jack");
        request.setEmail("jack@test.com");
        request.setPassword("Abc123");
        request.setRole(Role.STUDENT);

        User savedUser = new User(
                "Jack",
                "jack@test.com",
                passwordEncoder.encode("Abc123"),
                Role.STUDENT
        );

        UserEntity savedUserEntity = new UserEntity(
                1L,
                "Jack",
                "jack@test.com",
                passwordEncoder.encode("Abc123"),
                "STUDENT"
        );

        when(userRepository.findByEmail("jack@test.com")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userRepository.findEntityByEmail("jack@test.com")).thenReturn(savedUserEntity);

        AuthResponse result = authService.register(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getToken());
        assertEquals("Jack", result.getName());
        assertEquals("jack@test.com", result.getEmail());
        assertEquals(Role.STUDENT, result.getRole());

        verify(userRepository, times(1)).findByEmail("jack@test.com");
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findEntityByEmail("jack@test.com");
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void register_nullRequest_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(null)
        );

        assertEquals("Request cannot be null", exception.getMessage());
        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).findEntityByEmail(any());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void register_missingField_throwsException() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Jack");
        request.setEmail("");
        request.setPassword("Abc123");
        request.setRole(Role.STUDENT);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertEquals("All fields must be filled in", exception.getMessage());
        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).findEntityByEmail(any());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void register_weakPassword_throwsException() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Jack");
        request.setEmail("jack@test.com");
        request.setPassword("abc123");
        request.setRole(Role.STUDENT);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertEquals(
                "Password must be at least 6 characters, contain 1 uppercase letter and 1 number",
                exception.getMessage()
        );
        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).findEntityByEmail(any());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void register_emailAlreadyExists_throwsException() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Jack");
        request.setEmail("jack@test.com");
        request.setPassword("Abc123");
        request.setRole(Role.STUDENT);

        User existingUser = new User(
                "Jack",
                "jack@test.com",
                passwordEncoder.encode("Abc123"),
                Role.STUDENT
        );

        when(userRepository.findByEmail("jack@test.com")).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertEquals("Email is already registered", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("jack@test.com");
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).findEntityByEmail(any());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_validRequest_returnsAuthResponse() {
        LoginRequest request = new LoginRequest();
        request.setEmail("jack@test.com");
        request.setPassword("Abc123");

        UserEntity existingUserEntity = new UserEntity(
                1L,
                "Jack",
                "jack@test.com",
                passwordEncoder.encode("Abc123"),
                "STUDENT"
        );

        when(userRepository.findEntityByEmail("jack@test.com")).thenReturn(existingUserEntity);
        when(jwtService.generateToken("jack@test.com", "STUDENT")).thenReturn("fake-jwt-token");

        AuthResponse result = authService.login(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("fake-jwt-token", result.getToken());
        assertEquals("Jack", result.getName());
        assertEquals("jack@test.com", result.getEmail());
        assertEquals(Role.STUDENT, result.getRole());

        verify(userRepository, times(1)).findEntityByEmail("jack@test.com");
        verify(jwtService, times(1)).generateToken("jack@test.com", "STUDENT");
    }

    @Test
    void login_nullRequest_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(null)
        );

        assertEquals("Request cannot be null", exception.getMessage());
        verify(userRepository, never()).findEntityByEmail(any());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_missingField_throwsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("");
        request.setPassword("Abc123");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
        );

        assertEquals("All fields must be filled in", exception.getMessage());
        verify(userRepository, never()).findEntityByEmail(any());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_userNotFound_throwsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("jack@test.com");
        request.setPassword("Abc123");

        when(userRepository.findEntityByEmail("jack@test.com")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository, times(1)).findEntityByEmail("jack@test.com");
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_invalidPassword_throwsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("jack@test.com");
        request.setPassword("Wrong123");

        UserEntity existingUserEntity = new UserEntity(
                1L,
                "Jack",
                "jack@test.com",
                passwordEncoder.encode("Abc123"),
                "STUDENT"
        );

        when(userRepository.findEntityByEmail("jack@test.com")).thenReturn(existingUserEntity);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository, times(1)).findEntityByEmail("jack@test.com");
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }
}