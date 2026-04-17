package com.studyconnect.business.service;

import com.studyconnect.presentation.dto.CompleteTeacherProfileRequest;
import com.studyconnect.presentation.dto.TeacherResponse;

import java.util.List;

public interface ITeacherService {
    List<TeacherResponse> getAllTeachers();
    void completeTeacherProfile(Long teacherId, CompleteTeacherProfileRequest request);
}