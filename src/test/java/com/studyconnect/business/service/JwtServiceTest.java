package com.studyconnect.business.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secretKey",
                "VGhpc0lzQVN1cGVyU2VjcmV0S2V5Rm9ySldUVG9rZW5UZXN0aW5nMTIzNDU2"
        );

        ReflectionTestUtils.setField(
                jwtService,
                "jwtExpiration",
                86400000L
        );
    }

    @Test
    void generateToken_validData_returnsToken() {
        String token = jwtService.generateToken("jack@test.com", "STUDENT");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractEmail_validToken_returnsEmail() {
        String token = jwtService.generateToken("jack@test.com", "STUDENT");

        String email = jwtService.extractEmail(token);

        assertEquals("jack@test.com", email);
    }

    @Test
    void extractRole_validToken_returnsRole() {
        String token = jwtService.generateToken("jack@test.com", "TEACHER");

        String role = jwtService.extractRole(token);

        assertEquals("TEACHER", role);
    }

    @Test
    void isTokenValid_matchingEmail_returnsTrue() {
        String token = jwtService.generateToken("jack@test.com", "STUDENT");

        boolean result = jwtService.isTokenValid(token, "jack@test.com");

        assertTrue(result);
    }

    @Test
    void isTokenValid_wrongEmail_returnsFalse() {
        String token = jwtService.generateToken("jack@test.com", "STUDENT");

        boolean result = jwtService.isTokenValid(token, "other@test.com");

        assertFalse(result);
    }

    @Test
    void isTokenValid_expiredToken_returnsFalse() {
        ReflectionTestUtils.setField(
                jwtService,
                "jwtExpiration",
                -1000L
        );

        String token = jwtService.generateToken("jack@test.com", "STUDENT");

        boolean result = jwtService.isTokenValid(token, "jack@test.com");

        assertFalse(result);
    }

    @Test
    void isTokenValid_invalidToken_returnsFalse() {
        boolean result = jwtService.isTokenValid(
                "invalid.token.value",
                "jack@test.com"
        );

        assertFalse(result);
    }

    @Test
    void extractEmail_invalidToken_throwsException() {
        assertThrows(
                Exception.class,
                () -> jwtService.extractEmail("invalid.token.value")
        );
    }

    @Test
    void generateToken_invalidSecret_throwsException() {
        ReflectionTestUtils.setField(
                jwtService,
                "secretKey",
                "invalid"
        );

        assertThrows(
                Exception.class,
                () -> jwtService.generateToken("jack@test.com", "STUDENT")
        );
    }
}