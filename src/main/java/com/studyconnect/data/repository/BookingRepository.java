package com.studyconnect.data.repository;

import com.studyconnect.data.entity.BookingRequestEntity;
import com.studyconnect.data.irepository.IBookingRepository;
import com.studyconnect.data.mapper.BookingRequestMapper;
import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.domain.model.BookingStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookingRepository implements IBookingRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BookingRequest saveForSlotAndRequester(BookingRequest bookingRequest, Long slotId, Long requesterId) {
        BookingRequestEntity entity = BookingRequestMapper.toEntity(bookingRequest, slotId, requesterId);

        String sql = """
                INSERT INTO booking_request
                (slot_id, requester_id, start_time, end_time, title, description, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                entity.getSlotId(),
                entity.getRequesterId(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus()
        );

        return bookingRequest;
    }

    @Override
    public BookingRequest findById(Long bookingId) {
        String sql = """
                SELECT id, slot_id, requester_id, start_time, end_time, title, description, status
                FROM booking_request
                WHERE id = ?
                """;

        BookingRequestEntity entity = jdbcTemplate.query(
                sql,
                rs -> {
                    if (rs.next()) {
                        return new BookingRequestEntity(
                                rs.getLong("id"),
                                rs.getLong("slot_id"),
                                rs.getLong("requester_id"),
                                rs.getTime("start_time").toLocalTime(),
                                rs.getTime("end_time").toLocalTime(),
                                rs.getString("title"),
                                rs.getString("description"),
                                rs.getString("status")
                        );
                    }
                    return null;
                },
                bookingId
        );

        return BookingRequestMapper.toDomain(entity);
    }

    @Override
    public Long findSlotIdByBookingId(Long bookingId) {
        String sql = """
                SELECT slot_id
                FROM booking_request
                WHERE id = ?
                """;

        List<Long> results = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getLong("slot_id"),
                bookingId
        );

        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public void updateStatus(Long bookingId, BookingStatus status) {
        String sql = """
                UPDATE booking_request
                SET status = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(sql, status.name(), bookingId);
    }
}