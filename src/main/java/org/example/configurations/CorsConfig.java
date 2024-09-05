package org.example.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Вказуємо маршрути, на які діє CORS
                        .allowedOrigins("http://example.com") // Дозволені домени
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Дозволені HTTP методи
                        .allowedHeaders("*") // Дозволені заголовки
                        .allowCredentials(true); // Дозвіл на відправку cookies
            }
        };
    }
}
