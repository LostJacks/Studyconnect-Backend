package com.studyconnect.data.irepository;

import com.studyconnect.data.entity.AvailabilitySlotEntity;
import com.studyconnect.domain.model.AvailabilitySlot;

import java.time.LocalDate;
import java.util.List;

public interface IAvailabilityRepository {

    AvailabilitySlot saveForTeacher(AvailabilitySlot slot, Long teacherId);

    List<AvailabilitySlot> findByTeacherId(Long teacherId);

    List<AvailabilitySlotEntity> findEntitiesByTeacherId(Long teacherId);

    List<AvailabilitySlot> findByTeacherAndDate(Long teacherId, LocalDate date);

    AvailabilitySlot findById(Long id);

    Long findTeacherIdBySlotId(Long slotId);

    void markAsBooked(Long id);

    void updateBookedStatus(Long slotId, boolean booked);

    void updateAvailableStatus(Long slotId, boolean available);
}