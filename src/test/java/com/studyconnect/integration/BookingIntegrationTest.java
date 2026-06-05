package com.studyconnect.integration;

import com.studyconnect.business.service.JwtService;
import com.studyconnect.data.entity.UserEntity;
import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.domain.model.BookingStatus;
import com.studyconnect.domain.model.Role;
import com.studyconnect.domain.model.User;
import com.studyconnect.presentation.dto.CreateBookingRequest;
import com.studyconnect.presentation.dto.UpdateBookingStatusRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BookingIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IAvailabilityRepository availabilityRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    private TestUser createUser(String name, Role role) {
        String email = name.toLowerCase().replace(" ", "_")
                + "_" + System.nanoTime()
                + "@test.com";

        User user = new User(
                name,
                email,
                "Password123",
                role
        );

        userRepository.save(user);

        UserEntity savedUser = userRepository.findEntityByEmail(email);

        String token = jwtService.generateToken(
                savedUser.getEmail(),
                savedUser.getRole()
        );

        return new TestUser(savedUser.getId(), token);
    }

    private Long createSlot(Long teacherId) {
        AvailabilitySlot slot = new AvailabilitySlot(
                LocalDate.of(2026, 6, 3),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                false,
                true
        );

        availabilityRepository.saveForTeacher(slot, teacherId);

        return jdbcTemplate.queryForObject(
                """
                SELECT id
                FROM availability_slot
                WHERE teacher_id = ?
                ORDER BY id DESC
                LIMIT 1
                """,
                Long.class,
                teacherId
        );
    }

    private Long getLatestBookingId() {
        return jdbcTemplate.queryForObject(
                """
                SELECT id
                FROM booking_request
                ORDER BY id DESC
                LIMIT 1
                """,
                Long.class
        );
    }

    private Long createBooking(Long slotId, TestUser student) {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setSlotId(slotId);
        request.setRequesterId(student.id);
        request.setTitle("Java Help");
        request.setDescription("Need help with Spring Boot");

        restClient()
                .post()
                .uri("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + student.token)
                .body(request)
                .retrieve()
                .body(String.class);

        return getLatestBookingId();
    }

    @Test
    void shouldCreateBookingRequest() {
        TestUser teacher = createUser("Test Teacher", Role.TEACHER);
        TestUser student = createUser("Test Student", Role.STUDENT);

        Long slotId = createSlot(teacher.id);

        CreateBookingRequest request = new CreateBookingRequest();
        request.setSlotId(slotId);
        request.setRequesterId(student.id);
        request.setTitle("Java Help");
        request.setDescription("Need help with Spring Boot");

        String response = restClient()
                .post()
                .uri("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + student.token)
                .body(request)
                .retrieve()
                .body(String.class);

        assertNotNull(response);
        assertTrue(response.contains("Java Help"));
        assertTrue(response.contains("PENDING"));
    }

    @Test
    void shouldAcceptBookingRequestAndMarkSlotAsBooked() {
        TestUser teacher = createUser("Test Teacher", Role.TEACHER);
        TestUser student = createUser("Test Student", Role.STUDENT);

        Long slotId = createSlot(teacher.id);
        Long bookingId = createBooking(slotId, student);

        UpdateBookingStatusRequest updateRequest = new UpdateBookingStatusRequest();
        updateRequest.setTeacherId(teacher.id);
        updateRequest.setStatus(BookingStatus.ACCEPTED);

        String updatedBooking = restClient()
                .patch()
                .uri("/bookings/" + bookingId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + teacher.token)
                .body(updateRequest)
                .retrieve()
                .body(String.class);

        assertNotNull(updatedBooking);
        assertTrue(updatedBooking.contains("ACCEPTED"));
        assertTrue(availabilityRepository.findById(slotId).isBooked());
    }

    @Test
    void shouldRejectBookingRequest() {
        TestUser teacher = createUser("Test Teacher", Role.TEACHER);
        TestUser student = createUser("Test Student", Role.STUDENT);

        Long slotId = createSlot(teacher.id);
        Long bookingId = createBooking(slotId, student);

        UpdateBookingStatusRequest updateRequest = new UpdateBookingStatusRequest();
        updateRequest.setTeacherId(teacher.id);
        updateRequest.setStatus(BookingStatus.REJECTED);

        String updatedBooking = restClient()
                .patch()
                .uri("/bookings/" + bookingId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + teacher.token)
                .body(updateRequest)
                .retrieve()
                .body(String.class);

        assertNotNull(updatedBooking);
        assertTrue(updatedBooking.contains("REJECTED"));
    }

    static class TestUser {
        Long id;
        String token;

        TestUser(Long id, String token) {
            this.id = id;
            this.token = token;
        }
    }
}