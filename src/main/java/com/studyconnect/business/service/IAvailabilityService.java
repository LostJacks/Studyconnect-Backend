package com.studyconnect.business.service;

import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.presentation.dto.AvailabilitySlotResponse;
import com.studyconnect.presentation.dto.CreateAvailabilityRequest;

import java.util.List;

public interface IAvailabilityService {

    List<AvailabilitySlot> createAvailability(CreateAvailabilityRequest request);

    List<AvailabilitySlotResponse> getTeacherAvailability(Long teacherId);

    void updateAvailabilityStatus(Long slotId, boolean available);
}