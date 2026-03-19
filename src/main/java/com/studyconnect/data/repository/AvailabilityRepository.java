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
    public AvailabilitySlot save(AvailabilitySlot slot) {

        String sql = "INSERT INTO availability_slot (helper_id, date, start_time, end_time, booked) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                sql,
                slot.getHelperId(),
                slot.getDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                false
        );

        return slot;
    }

    @Override
    public List<AvailabilitySlot> findByHelperId(Long helperId) {

        String sql = """
                SELECT id, helper_id, date, start_time, end_time, booked
                FROM availability_slot
                WHERE helper_id = ?
                AND booked = FALSE
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                        new AvailabilitySlot(
                                rs.getLong("id"),
                                rs.getLong("helper_id"),
                                rs.getDate("date").toLocalDate(),
                                rs.getTime("start_time").toLocalTime(),
                                rs.getTime("end_time").toLocalTime(),
                                rs.getBoolean("booked")
                        ),
                helperId
        );
    }
}