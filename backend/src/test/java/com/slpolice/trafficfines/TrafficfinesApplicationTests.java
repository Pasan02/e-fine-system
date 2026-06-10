package com.slpolice.trafficfines;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Full Spring Boot context integration test.
 * Requires a live PostgreSQL database (configured via Docker Compose — see docs).
 * Run this in CI/CD where the DB is available, not as a standalone unit test.
 * Member 6 will wire this into the GitHub Actions CI pipeline.
 */
@SpringBootTest
@Disabled("Requires live PostgreSQL — run in CI/CD with Docker Compose")
class TrafficfinesApplicationTests {

	@Test
	void contextLoads() {
	}

}
