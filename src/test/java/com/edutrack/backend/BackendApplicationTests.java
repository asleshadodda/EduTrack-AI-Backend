package com.edutrack.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BackendApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void corsAllowsDeployedFrontendPreflight() throws Exception {
		mockMvc.perform(options("/api/students")
				.header("Origin", "https://edu-track-ai-frontend.vercel.app")
				.header("Access-Control-Request-Method", "GET"))
			.andExpect(status().isOk())
			.andExpect(header().string(
				"Access-Control-Allow-Origin",
				"https://edu-track-ai-frontend.vercel.app"))
			.andExpect(header().string(
				"Access-Control-Allow-Methods",
				containsString("GET")));
	}

	@Test
	void corsAllowsLocalFrontendPreflight() throws Exception {
		mockMvc.perform(options("/api/students")
				.header("Origin", "http://localhost:5173")
				.header("Access-Control-Request-Method", "GET"))
			.andExpect(status().isOk())
			.andExpect(header().string(
				"Access-Control-Allow-Origin",
				"http://localhost:5173"));
	}

	@Test
	void corsRejectsUnlistedOrigins() throws Exception {
		mockMvc.perform(options("/api/students")
				.header("Origin", "https://untrusted.example")
				.header("Access-Control-Request-Method", "GET"))
			.andExpect(status().isForbidden())
			.andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
	}

}
