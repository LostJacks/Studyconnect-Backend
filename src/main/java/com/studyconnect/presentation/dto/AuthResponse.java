package com.studyconnect.presentation.dto;

import com.studyconnect.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private long id;
    private String token;
    private String name;
    private String email;
    private Role role;
}