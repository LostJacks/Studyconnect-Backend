package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.IAvailabilityService;
import com.studyconnect.business.service.ITeacherService;
import com.studyconnect.presentation.dto.AvailabilitySlotResponse;
import com.studyconnect.presentation.dto.CompleteTeacherProfileRequest;
import com.studyconnect.presentation.dto.PagedResponse;
import com.studyconnect.presentation.dto.TeacherResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeacherControllerTest {

    private final ITeacherService teacherService = mock(ITeacherService.class);
    private final IAvailabilityService availabilityService = mock(IAvailabilityService.class);

    private final TeacherController teacherController =
            new TeacherController(teacherService, availabilityService);

    @Test
    void getTeachers_returnsPagedTeachers() {
        PagedResponse<TeacherResponse> response = new PagedResponse<>(
                List.of(),
                0,
                0,
                0,
                6
        );

        when(teacherService.getTeachers(0, 6)).thenReturn(response);

        PagedResponse<TeacherResponse> result = teacherController.getTeachers(0, 6);

        assertSame(response, result);
        verify(teacherService).getTeachers(0, 6);
    }

    @Test
    void getAvailableTeachers_returnsAvailableTeachers() {
        LocalDate date = LocalDate.of(2026, 5, 30);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(10, 30);

        List<TeacherResponse> response = List.of(
                new TeacherResponse(
                        1L,
                        "Teacher User",
                        "Bio",
                        "image.png",
                        List.of()
                )
        );

        when(teacherService.getAvailableTeachers(date, startTime, endTime)).thenReturn(response);

        List<TeacherResponse> result =
                teacherController.getAvailableTeachers(date, startTime, endTime);

        assertSame(response, result);
        verify(teacherService).getAvailableTeachers(date, startTime, endTime);
    }

    @Test
    void getTeacherAvailability_returnsAvailabilitySlots() {
        List<AvailabilitySlotResponse> response = List.of(
                new AvailabilitySlotResponse(
                        1L,
                        LocalDate.of(2026, 5, 30),
                        LocalTime.of(10, 0),
                        LocalTime.of(10, 30),
                        false,
                        true
                )
        );

        when(availabilityService.getTeacherAvailability(1L)).thenReturn(response);

        List<AvailabilitySlotResponse> result =
                teacherController.getTeacherAvailability(1L);

        assertSame(response, result);
        verify(availabilityService).getTeacherAvailability(1L);
    }

    @Test
    void getTeacherById_returnsTeacher() {
        TeacherResponse response =
                new TeacherResponse(
                        1L,
                        "Teacher User",
                        "Bio",
                        "image.png",
                        List.of()
                );

        when(teacherService.getTeacherById(1L)).thenReturn(response);

        TeacherResponse result = teacherController.getTeacherById(1L);

        assertSame(response, result);
        verify(teacherService).getTeacherById(1L);
    }

    @Test
    void completeProfile_updatesProfileAndReturnsOk() {
        CompleteTeacherProfileRequest request = new CompleteTeacherProfileRequest();
        request.setBio("I teach Java");
        request.setProfileImageUrl("image.png");
        request.setSkillIds(List.of(1L, 2L));

        ResponseEntity<Void> result = teacherController.completeProfile(1L, request);

        assertEquals(200, result.getStatusCode().value());
        verify(teacherService).completeTeacherProfile(1L, request);
    }
}