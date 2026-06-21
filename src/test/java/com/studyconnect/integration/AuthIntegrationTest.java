package com.studyconnect.integration;

import com.studyconnect.domain.model.Role;
import com.studyconnect.presentation.dto.LoginRequest;
import com.studyconnect.presentation.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void shouldRegisterTeacher() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Auth Teacher");
        request.setEmail("auth_teacher_" + System.nanoTime() + "@test.com");
        request.setPassword("Password123");
        request.setRole(Role.TEACHER);

        String response = register(request);

        assertNotNull(response);
        assertTrue(response.contains("Auth Teacher"));
        assertTrue(response.contains("TEACHER"));
    }

    @Test
    void shouldLoginAfterRegistering() {
        String email = "auth_student_" + System.nanoTime() + "@test.com";

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Auth Student");
        registerRequest.setEmail(email);
        registerRequest.setPassword("Password123");
        registerRequest.setRole(Role.STUDENT);

        register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword("Password123");

        String loginResponse = login(loginRequest);

        assertNotNull(loginResponse);
        assertTrue(loginResponse.contains("token"));
        assertTrue(loginResponse.contains("Auth Student"));
        assertTrue(loginResponse.contains("STUDENT"));
    }

    @Test
    void shouldRejectInvalidPasswordOnRegister() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Bad Password User");
        request.setEmail("bad_password_" + System.nanoTime() + "@test.com");
        request.setPassword("password");
        request.setRole(Role.STUDENT);

        HttpClientErrorException exception = assertThrows(
                HttpClientErrorException.class,
                () -> register(request)
        );

        assertEquals(400, exception.getStatusCode().value());
    }

    private String register(RegisterRequest request) {
        return restClient()
                .post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);
    }

    private String login(LoginRequest request) {
        return restClient()
                .post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);
    }
}