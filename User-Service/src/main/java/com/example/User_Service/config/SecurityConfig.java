package com.example.User_Service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.User_Service.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("🔐 Configuring Security Filter Chain...");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth

                        // ================== PUBLIC ==================
                        .requestMatchers(
                                "/api/auth/**",
                                "/actuator/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // ================== FICHIERS (PUBLIC) ==================
                        .requestMatchers("/api/files/**").permitAll()

                        // ================== DEBUG & TEST (PUBLIC) ==================
                        .requestMatchers("/api/users/debug/**").permitAll()
                        .requestMatchers("/api/users/test").permitAll()

                        // ============================================================
                        // ✅ INTER-SERVICES: Endpoints DTO accessibles sans auth
                        // Ces endpoints sont appelés par d'autres microservices via OpenFeign
                        // ============================================================
                        .requestMatchers(HttpMethod.GET, "/api/users/*/dto").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/username/*/dto").permitAll()

                        // ✅ Permettre aussi l'accès GET par ID pour les services internes
                        .requestMatchers(HttpMethod.GET, "/api/users/*").permitAll()

                        // ============================================================
                        // 🔓 TEMPORAIRE: Autoriser POST /api/users sans auth (pour créer directeurs)
                        // ⚠️ À SÉCURISER EN PRODUCTION !
                        // ============================================================
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

                        // ================== DIRECTEUR - Routes spécifiques ==================
                        .requestMatchers(HttpMethod.GET, "/api/users/etat/**")
                        .hasAnyRole("ADMIN", "DIRECTEUR_THESE")

                        .requestMatchers(HttpMethod.GET, "/api/users/role/**")
                        .hasAnyRole("ADMIN", "DIRECTEUR_THESE")

                        .requestMatchers(HttpMethod.PUT, "/api/users/*/validate-directeur")
                        .hasAnyRole("ADMIN", "DIRECTEUR_THESE")

                        .requestMatchers(HttpMethod.PUT, "/api/users/*/refuse-directeur")
                        .hasAnyRole("ADMIN", "DIRECTEUR_THESE")

                        // ================== ADMIN - Autres routes users (PUT, DELETE) ==================
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")

                        // ================== RESTE ==================
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("🔐 Security Filter Chain configured successfully");
        log.info("🔓 Inter-service endpoints (/api/users/*/dto) are PUBLIC");
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(
                Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        );
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // ✅ correct
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }



    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}