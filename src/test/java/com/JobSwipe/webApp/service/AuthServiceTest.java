package com.JobSwipe.webApp.service;

import com.JobSwipe.webApp.entities.UserConfig;
import com.JobSwipe.webApp.model.AuthenticationRequest;
import com.JobSwipe.webApp.model.AuthenticationResponse;
import com.JobSwipe.webApp.model.RegistrationRequest;
import com.JobSwipe.webApp.model.enums.Role;
import com.JobSwipe.webApp.repository.UserConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserConfigRepository userConfigRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    // --- registerUser ---

    @Test
    void registerUser_savesUserAndReturnsToken() {
        RegistrationRequest req = RegistrationRequest.builder()
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("secret")
                .build();

        when(passwordEncoder.encode("secret")).thenReturn("hashed_secret");
        when(jwtService.generateToken(any(UserConfig.class))).thenReturn("jwt-token");
        when(userConfigRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AuthenticationResponse response = authService.registerUser(req);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getCreatedAt()).isNotNull();

        ArgumentCaptor<UserConfig> captor = ArgumentCaptor.forClass(UserConfig.class);
        verify(userConfigRepository).save(captor.capture());
        UserConfig saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo("john");
        assertThat(saved.getEmail()).isEqualTo("john@example.com");
        assertThat(saved.getEncryptedPassword()).isEqualTo("hashed_secret");
        assertThat(saved.getRole()).isEqualTo(Role.USER);
    }

    // --- authenticate ---

    @Test
    void authenticate_returnsTokenForValidCredentials() {
        AuthenticationRequest req = AuthenticationRequest.builder()
                .username("john")
                .password("secret")
                .build();

        UserConfig user = UserConfig.builder()
                .userId(UUID.randomUUID())
                .username("john")
                .encryptedPassword("hashed")
                .role(Role.USER)
                .build();

        when(userConfigRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthenticationResponse response = authService.authenticate(req);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticate_throwsWhenUserNotFound() {
        AuthenticationRequest req = AuthenticationRequest.builder()
                .username("ghost")
                .password("pass")
                .build();

        when(userConfigRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.authenticate(req))
                .isInstanceOf(org.springframework.security.core.userdetails.UsernameNotFoundException.class);
    }

    @Test
    void authenticate_throwsWhenBadCredentials() {
        AuthenticationRequest req = AuthenticationRequest.builder()
                .username("john")
                .password("wrong")
                .build();

        doThrow(new BadCredentialsException("bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> authService.authenticate(req))
                .isInstanceOf(BadCredentialsException.class);
    }

    // --- getAuthenticatedUserId ---

    @Test
    void getAuthenticatedUserId_returnsUserIdFromSecurityContext() {
        UUID expectedId = UUID.randomUUID();
        UserConfig user = UserConfig.builder()
                .userId(expectedId)
                .username("john")
                .role(Role.USER)
                .build();

        var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.setContext(new SecurityContextImpl(auth));

        assertThat(authService.getAuthenticatedUserId()).isEqualTo(expectedId);

        SecurityContextHolder.clearContext();
    }
}
