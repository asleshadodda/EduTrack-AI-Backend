package com.edutrack.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global CORS configuration.
 *
 * In production set the ALLOWED_ORIGIN environment variable to the
 * deployed frontend URL, e.g. https://edu-track-ai-frontend.vercel.app
 *
 * Falls back to localhost:5173 for local development.
 */
@Configuration
public class CorsConfig {

    private static final String PRODUCTION_FRONTEND_ORIGIN =
            "https://edu-track-ai-frontend.vercel.app";

    @Value("${app.cors.allowed-origin:http://localhost:5173}")
    private String allowedOrigin;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(allowedOrigin, PRODUCTION_FRONTEND_ORIGIN)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }
}
