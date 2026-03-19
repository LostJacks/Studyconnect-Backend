package com.studyconnect.business.service;

import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.presentation.dto.CreateAvailabilityRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvailabilityService {

    private final IAvailabilityRepository availabilityRepository;

    public AvailabilityService(IAvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    public AvailabilitySlot createAvailability(CreateAvailabilityRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (request.getHelperId() == null ||
                request.getDate() == null ||
                request.getStartTime() == null ||
                request.getEndTime() == null) {

            throw new IllegalArgumentException("All fields must be filled in");
        }

        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        AvailabilitySlot slot = new AvailabilitySlot(
                null,
                request.getHelperId(),
                request.getDate(),
                request.getStartTime(),
                request.getEndTime(),
                false   // slot is available when created
        );

        return availabilityRepository.save(slot);
    }

    public List<AvailabilitySlot> getHelperAvailability(Long helperId) {

        if (helperId == null) {
            throw new IllegalArgumentException("HelperId cannot be null");
        }

        return availabilityRepository.findByHelperId(helperId);
    }
}