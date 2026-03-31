package com.studyconnect.data.mapper;

import com.studyconnect.data.entity.BookingRequestEntity;
import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.domain.model.BookingStatus;

public class BookingRequestMapper {

    public static BookingRequest toDomain(BookingRequestEntity entity) {
        if (entity == null) {
            return null;
        }

        return new BookingRequest(
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getTitle(),
                entity.getDescription(),
                BookingStatus.valueOf(entity.getStatus())
        );
    }

    public static BookingRequestEntity toEntity(
            BookingRequest domain,
            Long slotId,
            Long requesterId
    ) {
        if (domain == null) {
            return null;
        }

        return new BookingRequestEntity(
                null,
                slotId,
                requesterId,
                domain.getStartTime(),
                domain.getEndTime(),
                domain.getTitle(),
                domain.getDescription(),
                domain.getStatus().name()
        );
    }
}