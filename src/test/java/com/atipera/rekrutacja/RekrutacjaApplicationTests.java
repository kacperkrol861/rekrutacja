package com.atipera.rekrutacja;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootTest
class RekrutacjaApplicationTests {

	@TestConfiguration
	static class TestConfig {
		@Bean
		public RestClient.Builder restClientBuilder() {
			return RestClient.builder();
		}
	}

	@Test
	void contextLoads() {
	}

}