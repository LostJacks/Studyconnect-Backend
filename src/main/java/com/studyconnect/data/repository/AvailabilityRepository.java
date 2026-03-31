package com.studyconnect.data.repository;

import com.studyconnect.data.entity.AvailabilitySlotEntity;
import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.data.mapper.AvailabilitySlotMapper;
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
    public AvailabilitySlot saveForTeacher(AvailabilitySlot slot, Long teacherId) {
        AvailabilitySlotEntity entity = AvailabilitySlotMapper.toEntity(slot, teacherId);

        String sql = """
                INSERT INTO availability_slot (teacher_id, date, start_time, end_time, booked)
                VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                entity.getTeacherId(),
                entity.getDate(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.isBooked()
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

        List<AvailabilitySlotEntity> entities = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new AvailabilitySlotEntity(
                        rs.getLong("id"),
                        rs.getLong("teacher_id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getTime("start_time").toLocalTime(),
                        rs.getTime("end_time").toLocalTime(),
                        rs.getBoolean("booked")
                ),
                teacherId
        );

        return entities.stream()
                .map(AvailabilitySlotMapper::toDomain)
                .toList();
    }

    @Override
    public AvailabilitySlot findById(Long id) {
        String sql = """
                SELECT id, teacher_id, date, start_time, end_time, booked
                FROM availability_slot
                WHERE id = ?
                """;

        AvailabilitySlotEntity entity = jdbcTemplate.query(
                sql,
                rs -> {
                    if (rs.next()) {
                        return new AvailabilitySlotEntity(
                                rs.getLong("id"),
                                rs.getLong("teacher_id"),
                                rs.getDate("date").toLocalDate(),
                                rs.getTime("start_time").toLocalTime(),
                                rs.getTime("end_time").toLocalTime(),
                                rs.getBoolean("booked")
                        );
                    }
                    return null;
                },
                id
        );

        return AvailabilitySlotMapper.toDomain(entity);
    }

    @Override
    public Long findTeacherIdBySlotId(Long slotId) {
        String sql = """
                SELECT teacher_id
                FROM availability_slot
                WHERE id = ?
                """;

        List<Long> results = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getLong("teacher_id"),
                slotId
        );

        return results.isEmpty() ? null : results.get(0);
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
}