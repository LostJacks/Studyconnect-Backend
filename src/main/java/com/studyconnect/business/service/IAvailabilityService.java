package com.studyconnect.business.service;

import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.presentation.dto.CreateAvailabilityRequest;

import java.util.List;

public interface IAvailabilityService {
    List<AvailabilitySlot> createAvailability(CreateAvailabilityRequest request);
    List<AvailabilitySlot> getTeacherAvailability(Long teacherId);
}