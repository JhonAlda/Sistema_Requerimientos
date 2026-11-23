package com.gestionalmacen.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionalmacen.dto.response.ApiResponse;
import com.gestionalmacen.security.UsuarioDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
        http
            .cors(cors -> {})
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .exceptionHandling(errors -> errors
                .authenticationEntryPoint((request, response, exception) -> writeSecurityError(
                    response, objectMapper, HttpServletResponse.SC_UNAUTHORIZED,
                    "Debes iniciar sesión para realizar esta operación"))
                .accessDeniedHandler((request, response, exception) -> writeSecurityError(
                    response, objectMapper, HttpServletResponse.SC_FORBIDDEN,
                    "No tienes permisos para realizar esta operación")))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/img/**", "/api/auth/login", "/api/auth/logout", "/api/auth/csrf", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/api/usuarios/**", "/api/roles/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/reportes/**").hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                .requestMatchers("/api/despachos/**", "/api/inventario/**").hasAnyRole("ALMACENERO", "SUPERVISOR", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/api/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/requerimientos/**").hasAnyRole("SOLICITANTE", "SUPERVISOR", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.POST, "/api/sedes/**", "/api/almacenes/**", "/api/areas/**", "/api/categorias/**", "/api/unidades/**", "/api/productos/**").hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                .requestMatchers(HttpMethod.PUT, "/api/sedes/**", "/api/almacenes/**", "/api/areas/**", "/api/categorias/**", "/api/unidades/**", "/api/productos/**").hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                .requestMatchers(HttpMethod.DELETE, "/api/sedes/**", "/api/almacenes/**", "/api/areas/**", "/api/categorias/**", "/api/unidades/**", "/api/productos/**").hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                .requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PATCH, "/api/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMINISTRADOR")
                .anyRequest().authenticated());
        return http.build();
    }

    private void writeSecurityError(HttpServletResponse response, ObjectMapper objectMapper,
                                    int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), ApiResponse.failure(message, java.util.List.of(), java.util.Map.of()));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
