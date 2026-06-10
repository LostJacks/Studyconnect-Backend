package com.studyconnect.data.repository;

import com.studyconnect.data.entity.BookingRequestEntity;
import com.studyconnect.data.irepository.IBookingRepository;
import com.studyconnect.data.mapper.BookingRequestMapper;
import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.domain.model.BookingStatus;
import com.studyconnect.presentation.dto.BookingSessionResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookingRepository implements IBookingRepository {

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SLOT_ID = "slot_id";
    private static final String COLUMN_REQUESTER_ID = "requester_id";
    private static final String COLUMN_START_TIME = "start_time";
    private static final String COLUMN_END_TIME = "end_time";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_BOOKING_ID = "booking_id";
    private static final String COLUMN_STUDENT_ID = "student_id";
    private static final String COLUMN_STUDENT_NAME = "student_name";
    private static final String COLUMN_TEACHER_ID = "teacher_id";
    private static final String COLUMN_TEACHER_NAME = "teacher_name";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_PROFILE_IMAGE_URL = "profile_image_url";

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
                                rs.getLong(COLUMN_ID),
                                rs.getLong(COLUMN_SLOT_ID),
                                rs.getLong(COLUMN_REQUESTER_ID),
                                rs.getTime(COLUMN_START_TIME).toLocalTime(),
                                rs.getTime(COLUMN_END_TIME).toLocalTime(),
                                rs.getString(COLUMN_TITLE),
                                rs.getString(COLUMN_DESCRIPTION),
                                rs.getString(COLUMN_STATUS)
                        );
                    }
                    return null;
                },
                bookingId
        );

        return BookingRequestMapper.toDomain(entity);
    }

    @Override
    public List<BookingSessionResponse> findBookingsByUserId(Long userId) {
        String sql = """
        SELECT
            br.id AS booking_id,
            br.requester_id AS student_id,
            student.name AS student_name,
            av.teacher_id AS teacher_id,
            teacher.name AS teacher_name,
            br.slot_id,
            av.date,
            br.start_time,
            br.end_time,
            br.title,
            br.description,
            br.status,
            CASE
                WHEN br.requester_id = ? THEN teacher.profile_image_url
                ELSE student.profile_image_url
            END AS profile_image_url
        FROM booking_request br
        JOIN availability_slot av ON br.slot_id = av.id
        JOIN users student ON br.requester_id = student.id
        JOIN users teacher ON av.teacher_id = teacher.id
        WHERE br.requester_id = ?
        OR av.teacher_id = ?
        ORDER BY av.date DESC, br.start_time DESC
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new BookingSessionResponse(
                        rs.getLong(COLUMN_BOOKING_ID),
                        rs.getLong(COLUMN_STUDENT_ID),
                        rs.getString(COLUMN_STUDENT_NAME),
                        rs.getLong(COLUMN_TEACHER_ID),
                        rs.getString(COLUMN_TEACHER_NAME),
                        rs.getLong(COLUMN_SLOT_ID),
                        rs.getDate(COLUMN_DATE).toLocalDate(),
                        rs.getTime(COLUMN_START_TIME).toLocalTime(),
                        rs.getTime(COLUMN_END_TIME).toLocalTime(),
                        rs.getString(COLUMN_TITLE),
                        rs.getString(COLUMN_DESCRIPTION),
                        BookingStatus.valueOf(rs.getString(COLUMN_STATUS)),
                        rs.getString(COLUMN_PROFILE_IMAGE_URL)
                ),
                userId,
                userId,
                userId
        );
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
                (rs, rowNum) -> rs.getLong(COLUMN_SLOT_ID),
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