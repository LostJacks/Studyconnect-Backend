package com.studyconnect.business.service;


import com.studyconnect.domain.entity.AvailabilitySlot;
import com.studyconnect.presentation.dto.CreateAvailabilityRequest;
import org.springframework.stereotype.Service;

@Service
public class AvailabilityService {

    public AvailabilitySlot createAvailability(CreateAvailabilityRequest request) {

        // check if required fields are missing
        if (request.getHelperId() == null ||
                request.getDate() == null ||
                request.getStartTime() == null ||
                request.getEndTime() == null) {

            throw new IllegalArgumentException("All fields must be filled in");
        }

        // start time must be before end time
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // create the domain object
        return new AvailabilitySlot(
                request.getHelperId(),
                request.getDate(),
                request.getStartTime(),
                request.getEndTime()
        );
    }
}