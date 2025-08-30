package com.example.wallet_app.config.auth;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http
    ) throws Exception {

        http.cors(cors -> cors
                        .configurationSource(request -> {
                            CorsConfiguration config = new CorsConfiguration();
                            config.setAllowedOrigins(List.of("https://portal.wallet.co", "https://portal.dev.wallet.co"));
                            config.addAllowedOriginPattern("http://localhost:*");
                            config.setAllowedMethods(List.of("GET","PUT","DELETE" ,"PATCH","POST"));
                            config.setAllowCredentials(true); // Required for cookies/auth headers
                            config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                            return config;
                        })
                )
                .csrf(csrf -> csrf.disable()); // Disable CSRF for APIs

        return http.build();
    }

}

