package com.studyconnect.business.service;

import com.studyconnect.data.irepository.IUserRepository;
import org.springframework.stereotype.Service;

@Service
public class StudentService implements IStudentService {

    private final IUserRepository userRepository;

    public StudentService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void updateProfileImage(Long studentId, String profileImageUrl) {
        userRepository.updateProfileImage(studentId, profileImageUrl);
    }
}