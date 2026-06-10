package com.studyconnect.data.repository;

import com.studyconnect.data.entity.AvailabilitySlotEntity;
import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.data.mapper.AvailabilitySlotMapper;
import com.studyconnect.domain.model.AvailabilitySlot;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
                INSERT INTO availability_slot (teacher_id, date, start_time, end_time, booked, available)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                entity.getTeacherId(),
                entity.getDate(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.isBooked(),
                entity.isAvailable()
        );

        return slot;
    }

    @Override
    public List<AvailabilitySlot> findByTeacherId(Long teacherId) {
        String sql = """
                SELECT id, teacher_id, date, start_time, end_time, booked, available
                FROM availability_slot
                WHERE teacher_id = ?
                AND booked = FALSE
                AND available = TRUE
                AND (
                    date > CURDATE()
                    OR (date = CURDATE() AND start_time > CURTIME())
                )
                ORDER BY date, start_time
                """;

        List<AvailabilitySlotEntity> entities = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapRowToEntity(rs),
                teacherId
        );

        return entities.stream()
                .map(AvailabilitySlotMapper::toDomain)
                .toList();
    }

    @Override
    public List<AvailabilitySlotEntity> findEntitiesByTeacherId(Long teacherId) {
        String sql = """
                SELECT id, teacher_id, date, start_time, end_time, booked, available
                FROM availability_slot
                WHERE teacher_id = ?
                AND booked = FALSE
                AND (
                    date > CURDATE()
                    OR (date = CURDATE() AND start_time > CURTIME())
                )
                ORDER BY date, start_time
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapRowToEntity(rs),
                teacherId
        );
    }

    @Override
    public AvailabilitySlot findById(Long id) {
        String sql = """
                SELECT id, teacher_id, date, start_time, end_time, booked, available
                FROM availability_slot
                WHERE id = ?
                """;

        AvailabilitySlotEntity entity = jdbcTemplate.query(
                sql,
                rs -> {
                    if (rs.next()) {
                        return mapRowToEntity(rs);
                    }
                    return null;
                },
                id
        );

        return AvailabilitySlotMapper.toDomain(entity);
    }

    @Override
    public List<AvailabilitySlot> findByTeacherAndDate(Long teacherId, LocalDate date) {
        String sql = """
                SELECT id, teacher_id, date, start_time, end_time, booked, available
                FROM availability_slot
                WHERE teacher_id = ?
                AND date = ?
                AND booked = FALSE
                AND available = TRUE
                AND (
                    date > CURDATE()
                    OR (date = CURDATE() AND start_time > CURTIME())
                )
                ORDER BY date, start_time
                """;

        List<AvailabilitySlotEntity> entities = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapRowToEntity(rs),
                teacherId,
                date
        );

        return entities.stream()
                .map(AvailabilitySlotMapper::toDomain)
                .toList();
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
        String sql = """
                UPDATE availability_slot
                SET booked = TRUE
                WHERE id = ?
                """;

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
    public void updateAvailableStatus(Long slotId, boolean available) {
        String sql = """
                UPDATE availability_slot
                SET available = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(sql, available, slotId);
    }

    private AvailabilitySlotEntity mapRowToEntity(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new AvailabilitySlotEntity(
                rs.getLong("id"),
                rs.getLong("teacher_id"),
                rs.getDate("date").toLocalDate(),
                rs.getTime("start_time").toLocalTime(),
                rs.getTime("end_time").toLocalTime(),
                rs.getBoolean("booked"),
                rs.getBoolean("available")
        );
    }
}