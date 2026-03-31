package com.studyconnect.data.irepository;

import com.studyconnect.domain.model.User;

public interface IUserRepository {
    User save(User user);
    User findByEmail(String email);
    User findById(Long id);
}