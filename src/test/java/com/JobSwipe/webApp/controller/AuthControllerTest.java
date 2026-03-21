package com.JobSwipe.webApp.controller;

import com.JobSwipe.webApp.filter.JwtAuthenticationFilter;
import com.JobSwipe.webApp.model.AuthenticationRequest;
import com.JobSwipe.webApp.model.AuthenticationResponse;
import com.JobSwipe.webApp.model.RegistrationRequest;
import com.JobSwipe.webApp.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// All /api/auth/** endpoints are permitAll() — no security behavior to test here,
// so we disable Spring Security entirely to keep these tests focused on controller logic.
@WebMvcTest(
    controllers = AuthController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean AuthService authService;
    // Mocked so component scan can satisfy its deps without a real JwtService
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;

    private final AuthenticationResponse validResponse = AuthenticationResponse.builder()
            .token("jwt-token")
            .createdAt(LocalDateTime.now())
            .build();

    @Test
    void register_returns200WithToken() throws Exception {
        when(authService.registerUser(any())).thenReturn(validResponse);

        RegistrationRequest req = RegistrationRequest.builder()
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("secret123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void register_returns400WhenTokenIsBlank() throws Exception {
        AuthenticationResponse emptyToken = AuthenticationResponse.builder()
                .token("")
                .createdAt(LocalDateTime.now())
                .build();
        when(authService.registerUser(any())).thenReturn(emptyToken);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RegistrationRequest.builder()
                                .username("x").password("y").build()))
                        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void authenticate_returns200WithToken() throws Exception {
        when(authService.authenticate(any())).thenReturn(validResponse);

        AuthenticationRequest req = AuthenticationRequest.builder()
                .username("john")
                .password("secret")
                .build();

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void authenticate_returns400WhenTokenIsBlank() throws Exception {
        AuthenticationResponse emptyToken = AuthenticationResponse.builder()
                .token("").createdAt(LocalDateTime.now()).build();
        when(authService.authenticate(any())).thenReturn(emptyToken);

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                AuthenticationRequest.builder().username("x").password("wrong").build()))
                        )
                .andExpect(status().isBadRequest());
    }
}
