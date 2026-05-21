package com.studyconnect.business.service;

import com.studyconnect.presentation.dto.CompleteTeacherProfileRequest;
import com.studyconnect.presentation.dto.PagedResponse;
import com.studyconnect.presentation.dto.TeacherResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ITeacherService {

    List<TeacherResponse> getAllTeachers();

    PagedResponse<TeacherResponse> getTeachers(int page, int size);

    void completeTeacherProfile(
            Long teacherId,
            CompleteTeacherProfileRequest request
    );

    List<TeacherResponse> getAvailableTeachers(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    );
}