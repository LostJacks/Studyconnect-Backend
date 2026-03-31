package com.studyconnect.data.mapper;

import com.studyconnect.data.entity.AvailabilitySlotEntity;
import com.studyconnect.domain.model.AvailabilitySlot;

public class AvailabilitySlotMapper {

    public static AvailabilitySlot toDomain(AvailabilitySlotEntity entity) {
        if (entity == null) {
            return null;
        }

        return new AvailabilitySlot(
                entity.getDate(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.isBooked()
        );
    }

    public static AvailabilitySlotEntity toEntity(AvailabilitySlot domain, Long teacherId) {
        if (domain == null) {
            return null;
        }

        return new AvailabilitySlotEntity(
                null,
                teacherId,
                domain.getDate(),
                domain.getStartTime(),
                domain.getEndTime(),
                domain.isBooked()
        );
    }
}