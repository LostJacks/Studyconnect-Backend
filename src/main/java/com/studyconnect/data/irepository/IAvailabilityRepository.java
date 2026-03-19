package com.studyconnect.data.irepository;

import com.studyconnect.domain.model.AvailabilitySlot;

import java.util.List;

public interface IAvailabilityRepository {

    AvailabilitySlot save(AvailabilitySlot slot);

    List<AvailabilitySlot> findByHelperId(Long helperId);

    AvailabilitySlot findById(Long id);
}