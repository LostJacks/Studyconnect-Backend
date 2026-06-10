package com.studyconnect.business.service;

import com.studyconnect.data.irepository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class StudentServiceTest {

    private IUserRepository userRepository;
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        userRepository = mock(IUserRepository.class);
        studentService = new StudentService(userRepository);
    }

    @Test
    void updateProfileImage_validRequest_callsRepository() {
        Long studentId = 1L;
        String profileImageUrl = "male-student.png";

        studentService.updateProfileImage(studentId, profileImageUrl);

        verify(userRepository).updateProfileImage(
                studentId,
                profileImageUrl
        );
    }
}