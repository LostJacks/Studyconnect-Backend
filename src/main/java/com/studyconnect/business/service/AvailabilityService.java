package com.studyconnect.business.service;

import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.presentation.dto.CreateAvailabilityRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AvailabilityService implements IAvailabilityService {

    private final IAvailabilityRepository availabilityRepository;

    public AvailabilityService(IAvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    @Override
    public List<AvailabilitySlot> createAvailability(CreateAvailabilityRequest request) {
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        long totalMinutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();

        if (totalMinutes % 30 != 0) {
            throw new IllegalArgumentException("Time range must be divisible into 30-minute slots");
        }

        List<AvailabilitySlot> createdSlots = new ArrayList<>();
        LocalTime currentStart = request.getStartTime();

        while (currentStart.isBefore(request.getEndTime())) {
            LocalTime currentEnd = currentStart.plusMinutes(30);

            AvailabilitySlot slot = new AvailabilitySlot(
                    request.getDate(),
                    currentStart,
                    currentEnd,
                    false
            );

            AvailabilitySlot savedSlot = availabilityRepository.saveForTeacher(
                    slot,
                    request.getTeacherId()
            );

            createdSlots.add(savedSlot);
            currentStart = currentEnd;
        }

        return createdSlots;
    }

    @Override
    public List<AvailabilitySlot> getTeacherAvailability(Long teacherId) {
        return availabilityRepository.findByTeacherId(teacherId);
    }
}