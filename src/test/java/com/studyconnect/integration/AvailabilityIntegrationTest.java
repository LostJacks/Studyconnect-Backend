package com.studyconnect.integration;

import com.studyconnect.business.service.JwtService;
import com.studyconnect.data.entity.UserEntity;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.domain.model.Role;
import com.studyconnect.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AvailabilityIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    private TestTeacher createTeacher() {
        String email = "teacher_" + System.currentTimeMillis() + "@test.com";

        User teacher = new User(
                "Test Teacher",
                email,
                "Password123",
                Role.TEACHER
        );

        userRepository.save(teacher);

        UserEntity savedTeacher = userRepository.findEntityByEmail(email);

        String token = jwtService.generateToken(
                savedTeacher.getEmail(),
                savedTeacher.getRole()
        );

        return new TestTeacher(savedTeacher.getId(), token);
    }

    @Test
    void shouldCreateAvailabilitySlots() {
        TestTeacher teacher = createTeacher();

        CreateAvailabilityRequest request = new CreateAvailabilityRequest(
                teacher.id,
                "2026-06-03",
                "10:00",
                "11:00"
        );

        String response = restClient()
                .post()
                .uri("/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + teacher.token)
                .body(request)
                .retrieve()
                .body(String.class);

        assertNotNull(response);
        assertTrue(response.contains("10:00"));
        assertTrue(response.contains("10:30"));
    }

    @Test
    void shouldReturnBadRequestWhenEndTimeIsBeforeStartTime() {
        TestTeacher teacher = createTeacher();

        CreateAvailabilityRequest request = new CreateAvailabilityRequest(
                teacher.id,
                "2026-06-03",
                "12:00",
                "10:00"
        );

        HttpClientErrorException exception = assertThrows(
                HttpClientErrorException.class,
                () -> restClient()
                        .post()
                        .uri("/availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + teacher.token)
                        .body(request)
                        .retrieve()
                        .body(String.class)
        );

        assertEquals(400, exception.getStatusCode().value());
    }

    static class TestTeacher {
        Long id;
        String token;

        TestTeacher(Long id, String token) {
            this.id = id;
            this.token = token;
        }
    }

    static class CreateAvailabilityRequest {
        public Long teacherId;
        public String date;
        public String startTime;
        public String endTime;

        public CreateAvailabilityRequest(Long teacherId, String date, String startTime, String endTime) {
            this.teacherId = teacherId;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }
}