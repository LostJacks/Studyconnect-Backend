package com.studyconnect.data.repository;

import com.studyconnect.data.irepository.IBookingRepository;
import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.domain.model.BookingStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

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
                (slot_id, requester_id, start_time, end_time, title, description, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, bookingRequest.getSlotId());
            ps.setLong(2, bookingRequest.getRequesterId());
            ps.setObject(3, bookingRequest.getStartTime());
            ps.setObject(4, bookingRequest.getEndTime());
            ps.setString(5, bookingRequest.getTitle());
            ps.setString(6, bookingRequest.getDescription());
            ps.setString(7, bookingRequest.getStatus().name());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;

        return new BookingRequest(
                generatedId,
                bookingRequest.getSlotId(),
                bookingRequest.getRequesterId(),
                bookingRequest.getStartTime(),
                bookingRequest.getEndTime(),
                bookingRequest.getTitle(),
                bookingRequest.getDescription(),
                bookingRequest.getStatus()
        );
    }

    @Override
    public BookingRequest findById(Long bookingId) {
        String sql = """
                SELECT id, slot_id, requester_id, start_time, end_time, title, description, status
                FROM booking_request
                WHERE id = ?
                """;

        return jdbcTemplate.query(
                sql,
                rs -> {
                    if (rs.next()) {
                        return new BookingRequest(
                                rs.getLong("id"),
                                rs.getLong("slot_id"),
                                rs.getLong("requester_id"),
                                rs.getTime("start_time").toLocalTime(),
                                rs.getTime("end_time").toLocalTime(),
                                rs.getString("title"),
                                rs.getString("description"),
                                BookingStatus.valueOf(rs.getString("status"))
                        );
                    }
                    return null;
                },
                bookingId
        );
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