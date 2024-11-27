package com.congduc.apigateway.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // Cho phép gửi thông tin đăng nhập
        config.addAllowedOrigin("http://localhost:4200"); // Nguồn gốc của Angular
//        config.addAllowedOrigin("http://localhost:80");
        config.addAllowedHeader("*"); // Cho phép tất cả các header
        config.addAllowedMethod("*"); // Cho phép tất cả các phương thức HTTP
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
