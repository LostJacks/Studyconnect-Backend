package com.studyconnect.data.irepository;

import com.studyconnect.data.entity.UserEntity;
import com.studyconnect.domain.model.User;

import java.util.List;

public interface IUserRepository {

    User save(User user);
    User findByEmail(String email);
    User findById(Long id);

    List<UserEntity> findAll();

    void updateBio(Long userId, String bio);
    UserEntity findEntityByEmail(String email);
    void updateProfileImage(Long userId, String profileImageUrl);

}