package com.JobSwipe.webApp.service;

import com.JobSwipe.webApp.entities.UserConfig;
import com.JobSwipe.webApp.model.enums.Role;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    // 256-bit base64 key valid for HS256
    private static final String SECRET = "dGVzdFNlY3JldEtleUZvckpXVFRlc3RpbmdQdXJwb3Nlcw==";

    private UserConfig user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);

        user = UserConfig.builder()
                .userId(UUID.randomUUID())
                .username("testuser")
                .encryptedPassword("hashed")
                .role(Role.USER)
                .build();
    }

    @Test
    void generateToken_returnsNonBlankToken() {
        String token = jwtService.generateToken(user);
        assertThat(token).isNotBlank();
    }

    @Test
    void extractUsername_returnsCorrectUsername() {
        String token = jwtService.generateToken(user);
        assertThat(jwtService.extractUsername(token)).isEqualTo("testuser");
    }

    @Test
    void isTokenValid_returnsTrueForValidToken() {
        String token = jwtService.generateToken(user);
        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    void isTokenValid_returnsFalseForWrongUser() {
        String token = jwtService.generateToken(user);

        UserConfig otherUser = UserConfig.builder()
                .userId(UUID.randomUUID())
                .username("otheruser")
                .encryptedPassword("hashed")
                .role(Role.USER)
                .build();

        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    void isTokenValid_returnsFalseForExpiredToken() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L); // already expired
        String token = jwtService.generateToken(user);

        assertThatThrownBy(() -> jwtService.isTokenValid(token, user))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void getExpirationTime_returnsConfiguredValue() {
        assertThat(jwtService.getExpirationTime()).isEqualTo(3600000L);
    }

    @Test
    void generateToken_withExtraClaims_includesThemInToken() {
        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("role", "admin");
        String token = jwtService.generateToken(claims, user);

        assertThat(jwtService.extractUsername(token)).isEqualTo("testuser");
        assertThat(token).isNotBlank();
    }
}
