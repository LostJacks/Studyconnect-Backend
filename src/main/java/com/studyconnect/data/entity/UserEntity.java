package com.studyconnect.data.entity;

import lombok.Getter;

@Getter
public class UserEntity {
    private Long id;
    private String name;
    private String email;
    private String passwordHash;
    private String role;
    private String bio;
    private String skills;

    public UserEntity(Long id, String name, String email, String passwordHash, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.bio = null;
        this.skills = null;
    }

    public UserEntity(Long id, String name, String email, String passwordHash, String role, String bio, String skills) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.bio = bio;
        this.skills = skills;
    }
}