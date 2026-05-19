package com.studyconnect.business.service;

import com.studyconnect.data.entity.AvailabilitySlotEntity;
import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.presentation.dto.AvailabilitySlotResponse;
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
        validateTimeRange(request);

        List<AvailabilitySlot> createdSlots = new ArrayList<>();

        for (LocalTime currentStart = request.getStartTime();
             currentStart.isBefore(request.getEndTime());
             currentStart = currentStart.plusMinutes(30)) {

            AvailabilitySlot slot = createSlot(request, currentStart);

            AvailabilitySlot savedSlot = availabilityRepository.saveForTeacher(
                    slot,
                    request.getTeacherId()
            );

            createdSlots.add(savedSlot);
        }

        return createdSlots;
    }

    private void validateTimeRange(CreateAvailabilityRequest request) {
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        long totalMinutes = Duration.between(
                request.getStartTime(),
                request.getEndTime()
        ).toMinutes();

        if (totalMinutes % 30 != 0) {
            throw new IllegalArgumentException("Time range must be divisible into 30-minute slots");
        }
    }

    private AvailabilitySlot createSlot(CreateAvailabilityRequest request, LocalTime currentStart) {
        return new AvailabilitySlot(
                request.getDate(),
                currentStart,
                currentStart.plusMinutes(30),
                false
        );
    }

    @Override
    public List<AvailabilitySlotResponse> getTeacherAvailability(Long teacherId) {
        return availabilityRepository.findEntitiesByTeacherId(teacherId)
                .stream()
                .map(entity -> new AvailabilitySlotResponse(
                        entity.getId(),
                        entity.getDate(),
                        entity.getStartTime(),
                        entity.getEndTime(),
                        entity.isBooked()
                ))
                .toList();
    }
}