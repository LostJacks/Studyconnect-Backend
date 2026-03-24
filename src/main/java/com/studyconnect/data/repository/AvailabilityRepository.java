package com.studyconnect.data.repository;

import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.domain.model.AvailabilitySlot;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AvailabilityRepository implements IAvailabilityRepository {

    private final JdbcTemplate jdbcTemplate;

    public AvailabilityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void markAsBooked(Long id) {
        String sql = "UPDATE availability_slot SET booked = true WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void updateBookedStatus(Long slotId, boolean booked) {

        String sql = """
            UPDATE availability_slot
            SET booked = ?
            WHERE id = ?
            """;

        jdbcTemplate.update(sql, booked, slotId);
    }

    @Override
    public AvailabilitySlot save(AvailabilitySlot slot) {
        String sql = "INSERT INTO availability_slot (teacher_id, date, start_time, end_time, booked) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                sql,
                slot.getTeacherId(),
                slot.getDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                false
        );

        return slot;
    }

    @Override
    public List<AvailabilitySlot> findByTeacherId(Long teacherId) {
        String sql = """
                SELECT id, teacher_id, date, start_time, end_time, booked
                FROM availability_slot
                WHERE teacher_id = ?
                AND booked = FALSE
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                        new AvailabilitySlot(
                                rs.getLong("id"),
                                rs.getLong("teacher_id"),
                                rs.getDate("date").toLocalDate(),
                                rs.getTime("start_time").toLocalTime(),
                                rs.getTime("end_time").toLocalTime(),
                                rs.getBoolean("booked")
                        ),
                teacherId
        );
    }

    @Override
    public AvailabilitySlot findById(Long id) {
        String sql = """
                SELECT id, teacher_id, date, start_time, end_time, booked
                FROM availability_slot
                WHERE id = ?
                """;

        List<AvailabilitySlot> results = jdbcTemplate.query(sql, (rs, rowNum) ->
                        new AvailabilitySlot(
                                rs.getLong("id"),
                                rs.getLong("teacher_id"),
                                rs.getDate("date").toLocalDate(),
                                rs.getTime("start_time").toLocalTime(),
                                rs.getTime("end_time").toLocalTime(),
                                rs.getBoolean("booked")
                        ),
                id
        );

        return results.isEmpty() ? null : results.get(0);
    }
}