package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.IStudentService;
import com.studyconnect.presentation.dto.UpdateProfileImageRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class StudentControllerTest {

    private final IStudentService studentService = mock(IStudentService.class);

    private final StudentController studentController =
            new StudentController(studentService);

    @Test
    void updateProfilePicture_updatesImageAndReturnsOk() {
        UpdateProfileImageRequest request =
                new UpdateProfileImageRequest();

        request.setProfileImageUrl("profile.png");

        ResponseEntity<Void> response =
                studentController.updateProfilePicture(1L, request);

        assertEquals(200, response.getStatusCode().value());

        verify(studentService)
                .updateProfileImage(1L, "profile.png");
    }
}