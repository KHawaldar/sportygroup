package com.sportygroup.ticketing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class TicketingApplicationTests {
	@Container
	@ServiceConnection
	 static GenericContainer<?> redis = new GenericContainer<>("redis:7.2.4").withExposedPorts(6379);




	@Test
	void contextLoads() {
	}

}
