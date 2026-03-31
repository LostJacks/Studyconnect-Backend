package com.studyconnect.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserEntity {
    private Long id;
    private String name;
    private String email;
    private String passwordHash;
    private String role;
}