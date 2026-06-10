package com.studyconnect.integration;

import com.studyconnect.business.service.JwtService;
import com.studyconnect.business.service.message.IMessageService;
import com.studyconnect.data.entity.UserEntity;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.domain.model.Role;
import com.studyconnect.domain.model.User;
import com.studyconnect.presentation.dto.message.MessageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MessageIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IMessageService messageService;

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    private TestUser createStudent() {
        String email = "message_student_" + System.nanoTime() + "@test.com";

        User user = new User(
                "Message Student",
                email,
                "Password123",
                Role.STUDENT
        );

        userRepository.save(user);

        UserEntity savedUser = userRepository.findEntityByEmail(email);

        String token = jwtService.generateToken(
                savedUser.getEmail(),
                savedUser.getRole()
        );

        return new TestUser(savedUser.getId(), token);
    }

    @Test
    void shouldCreateChatAndReturnEmptyMessagesForBooking() {
        TestUser student = createStudent();
        Long bookingId = System.nanoTime();

        String response = restClient()
                .get()
                .uri("/messages/" + bookingId)
                .header("Authorization", "Bearer " + student.token)
                .retrieve()
                .body(String.class);

        assertNotNull(response);
        assertEquals("[]", response);
    }

    @Test
    void shouldSendMessageAndRetrieveItByBookingId() {
        TestUser student = createStudent();
        Long bookingId = System.nanoTime();

        MessageResponse savedMessage = messageService.sendMessage(
                bookingId,
                student.id,
                "Hello, I need help with Java."
        );

        assertNotNull(savedMessage);
        assertEquals(student.id, savedMessage.getSenderId());
        assertEquals("Hello, I need help with Java.", savedMessage.getContent());

        String response = restClient()
                .get()
                .uri("/messages/" + bookingId)
                .header("Authorization", "Bearer " + student.token)
                .retrieve()
                .body(String.class);

        assertNotNull(response);
        assertTrue(response.contains("Hello, I need help with Java."));
        assertTrue(response.contains(String.valueOf(student.id)));
    }

    @Test
    void shouldReturnMessagesInOrder() {
        TestUser student = createStudent();
        Long bookingId = System.nanoTime();

        messageService.sendMessage(bookingId, student.id, "First message");
        messageService.sendMessage(bookingId, student.id, "Second message");

        List<MessageResponse> messages = messageService.getMessages(bookingId);

        assertEquals(2, messages.size());
        assertEquals("First message", messages.get(0).getContent());
        assertEquals("Second message", messages.get(1).getContent());
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