package com.studyconnect.integration;

import com.studyconnect.business.service.JwtService;
import com.studyconnect.data.entity.UserEntity;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.domain.model.Role;
import com.studyconnect.domain.model.User;
import com.studyconnect.presentation.dto.CompleteTeacherProfileRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TeacherIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    private TestUser createTeacher() {
        String email = "teacher_" + System.nanoTime() + "@test.com";

        User teacher = new User(
                "Integration Teacher",
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

        return new TestUser(savedTeacher.getId(), token);
    }

    private Long createSkill(String name) {
        jdbcTemplate.update(
                "INSERT INTO skill (name) VALUES (?)",
                name
        );

        return jdbcTemplate.queryForObject(
                "SELECT id FROM skill WHERE name = ? ORDER BY id DESC LIMIT 1",
                Long.class,
                name
        );
    }

    @Test
    void shouldGetTeachersPaged() {
        TestUser teacher = createTeacher();

        String response = restClient()
                .get()
                .uri("/teachers?page=0&size=50")
                .header("Authorization", "Bearer " + teacher.token)
                .retrieve()
                .body(String.class);

        assertNotNull(response);
        assertTrue(response.contains("content"));
        assertTrue(response.contains("totalItems"));
        assertTrue(response.contains("pageSize"));
    }

    @Test
    void shouldGetTeacherById() {
        TestUser teacher = createTeacher();

        String response = restClient()
                .get()
                .uri("/teachers/" + teacher.id)
                .header("Authorization", "Bearer " + teacher.token)
                .retrieve()
                .body(String.class);

        assertNotNull(response);
        assertTrue(response.contains("Integration Teacher"));
    }

    @Test
    void shouldCompleteTeacherProfile() {
        TestUser teacher = createTeacher();
        Long skillId = createSkill("Java");

        CompleteTeacherProfileRequest request = new CompleteTeacherProfileRequest();
        request.setBio("I help students with Java and Spring Boot.");
        request.setProfileImageUrl("/avatars/male.png");
        request.setSkillIds(List.of(skillId));

        restClient()
                .put()
                .uri("/teachers/" + teacher.id + "/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + teacher.token)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        String bio = jdbcTemplate.queryForObject(
                "SELECT bio FROM users WHERE id = ?",
                String.class,
                teacher.id
        );

        Integer skillCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM teacher_skill WHERE teacher_id = ? AND skill_id = ?",
                Integer.class,
                teacher.id,
                skillId
        );

        assertEquals("I help students with Java and Spring Boot.", bio);
        assertEquals(1, skillCount);
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