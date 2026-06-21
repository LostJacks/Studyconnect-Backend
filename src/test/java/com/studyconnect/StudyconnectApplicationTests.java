package com.studyconnect;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Disabled because integration tests already verify the Spring context")
@SpringBootTest
class StudyconnectApplicationTests {

	@Test
	void contextLoads() {
	}
}