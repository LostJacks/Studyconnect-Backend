package com.studyconnect.domain.model;

import lombok.Getter;

@Getter
public class Skill {

    private final String name;

    public Skill(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Skill name cannot be empty");
        }

        this.name = name;
    }
}