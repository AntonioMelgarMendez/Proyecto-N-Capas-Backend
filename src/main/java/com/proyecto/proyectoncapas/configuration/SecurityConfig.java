package com.proyecto.proyectoncapas.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/payments/**").permitAll()
                        .requestMatchers("/api/webhooks/**").permitAll()
                        .requestMatchers("/api/contracts/**").permitAll()
                        .requestMatchers("/api/identity/**").permitAll()
                        .requestMatchers("/api/properties/**").permitAll()
                        .requestMatchers("/api/reviews/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}