package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.IStudentService;
import com.studyconnect.presentation.dto.UpdateProfileImageRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final IStudentService studentService;

    public StudentController(IStudentService studentService) {
        this.studentService = studentService;
    }

    @PutMapping("/{studentId}/profile-picture")
    public ResponseEntity<Void> updateProfilePicture(
            @PathVariable Long studentId,
            @Valid @RequestBody UpdateProfileImageRequest request
    ) {
        studentService.updateProfileImage(
                studentId,
                request.getProfileImageUrl()
        );

        return ResponseEntity.ok().build();
    }
}