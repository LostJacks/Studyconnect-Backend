package com.studyconnect.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class User {
    private String name;
    private String email;
    private String passwordHash;
    private Role role;
}
