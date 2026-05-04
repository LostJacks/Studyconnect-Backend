package com.studyconnect.data.mapper;

import com.studyconnect.data.entity.UserEntity;
import com.studyconnect.domain.model.Role;
import com.studyconnect.domain.model.User;

public class UserMapper {

    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return new User(
                entity.getName(),
                entity.getEmail(),
                entity.getPasswordHash(),
                Role.valueOf(entity.getRole())
        );
    }

    public static UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        return new UserEntity(
                null,
                user.getName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole().name(),
                null,
                null,
                null
        );
    }
}