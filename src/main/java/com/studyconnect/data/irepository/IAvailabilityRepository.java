package com.studyconnect.data.irepository;

import com.studyconnect.domain.model.AvailabilitySlot;

import java.util.List;

public interface IAvailabilityRepository {

    AvailabilitySlot save(AvailabilitySlot slot);

    List<AvailabilitySlot> findByTeacherId(Long helperId);

    AvailabilitySlot findById(Long id);

    void markAsBooked(Long id);
    void updateBookedStatus(Long slotId, boolean booked);
}