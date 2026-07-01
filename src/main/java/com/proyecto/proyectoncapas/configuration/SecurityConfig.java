package com.proyecto.proyectoncapas.configuration;

import com.proyecto.proyectoncapas.services.authentication.impl.UserDetailsServiceImpl;
import com.proyecto.proyectoncapas.utils.security.JwtAuthFilter;
import com.proyecto.proyectoncapas.utils.security.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    //Roles
    private static final String ADMIN = "ADMIN";
    private static final String ARRENDADOR = "ARRENDADOR";
    private static final String INQUILINO = "INQUILINO";


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Sin necesidad de autenticar
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/payments/**").permitAll()
                        .requestMatchers("/api/webhooks/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Administración — solo ADMIN
                        .requestMatchers("/api/admin/**").hasAuthority(ADMIN)

                        // Propiedades
                        // Solicitar catalogo de propiedades, ver/buscar seria publico
                        .requestMatchers(HttpMethod.GET, "/api/properties/**").permitAll()
                        // La gestion requiere roles administrativos
                        .requestMatchers(HttpMethod.POST, "/api/properties/**").hasAnyAuthority(ADMIN, ARRENDADOR)
                        .requestMatchers(HttpMethod.PUT, "/api/properties/**").hasAnyAuthority(ADMIN, ARRENDADOR)
                        .requestMatchers(HttpMethod.PATCH, "/api/properties/**").hasAnyAuthority(ADMIN, ARRENDADOR)
                        .requestMatchers(HttpMethod.DELETE, "/api/properties/**").hasAnyAuthority(ADMIN, ARRENDADOR)

                        // Reservas
                        // Cotización pública — permite ver precios sin estar autenticado
                        .requestMatchers(HttpMethod.POST, "/api/reservations/*/quote").permitAll()
                        // Operaciones de inquilino
                        .requestMatchers(HttpMethod.POST, "/api/reservations/*/book").hasAuthority(INQUILINO)
                        .requestMatchers(HttpMethod.POST, "/api/reservations/*/extend/**").hasAuthority(INQUILINO)
                        .requestMatchers(HttpMethod.POST, "/api/reservations/*/cancel/**").hasAuthority(INQUILINO)
                        // Operaciones de arrendador/admin
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/**").hasAnyAuthority(ARRENDADOR, ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/api/reservations/**").hasAnyAuthority(ARRENDADOR, ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/reservations/**").authenticated()

                        // Contratos
                        .requestMatchers("/api/contracts/**").authenticated()

                        // Mantenimiento preventivo
                        .requestMatchers("/api/preventive-tasks/**").hasAnyAuthority(ADMIN, ARRENDADOR)

                        // Mantenimiento reactivo (tickets)
                        .requestMatchers(HttpMethod.POST, "/api/maintenance/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/contracts/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/contracts/**").hasAnyAuthority(ADMIN, ARRENDADOR)
                        .requestMatchers(HttpMethod.PATCH, "/api/contracts/**").hasAnyAuthority(ADMIN, ARRENDADOR)

                        // Reviews
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/reviews/**").authenticated()

                        // Verificacion de identidad
                        // INQUILINO puede subir y consultar su propio documento
                        .requestMatchers(HttpMethod.POST,"/api/identity/**").authenticated()
                        .requestMatchers(HttpMethod.GET,"/api/identity/**").hasAnyAuthority(ADMIN, ARRENDADOR, INQUILINO)
                        .requestMatchers(HttpMethod.PUT,"/api/identity/**").hasAnyAuthority(ADMIN, ARRENDADOR)
                        .requestMatchers(HttpMethod.PATCH,"/api/identity/**").hasAnyAuthority(ADMIN, ARRENDADOR)

                        // Llaves temporales
                        .requestMatchers("/api/keys/**").hasAnyAuthority(ADMIN, ARRENDADOR)

                        // multas y politicas de ruidos
                        .requestMatchers(HttpMethod.POST, "/api/fines/**").hasAnyAuthority(ADMIN, ARRENDADOR)
                        .requestMatchers(HttpMethod.PUT, "/api/fines/**").hasAnyAuthority(ADMIN, ARRENDADOR)
                        .requestMatchers(HttpMethod.PATCH, "/api/fines/**").hasAnyAuthority(ADMIN, ARRENDADOR)
                        .requestMatchers(HttpMethod.GET, "/api/fines/**").authenticated()


                        .anyRequest().authenticated()

                        
                ).exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                ).addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:3000",
                "https://proyecto-n-capas-frontend.vercel.app"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    // Mismo encoder para guardar y comparar passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsServiceImpl userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

}