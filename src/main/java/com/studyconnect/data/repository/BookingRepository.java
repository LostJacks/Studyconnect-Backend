package com.studyconnect.data.repository;

import com.studyconnect.data.irepository.IBookingRepository;
import com.studyconnect.domain.model.BookingRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BookingRepository implements IBookingRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BookingRequest save(BookingRequest bookingRequest) {
        String sql = """
                INSERT INTO booking_request
                (slot_id, requester_id, start_time, end_time, title, description)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                bookingRequest.getSlotId(),
                bookingRequest.getRequesterId(),
                bookingRequest.getStartTime(),
                bookingRequest.getEndTime(),
                bookingRequest.getTitle(),
                bookingRequest.getDescription()
        );

        return bookingRequest;
    }
}