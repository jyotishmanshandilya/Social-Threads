package com.JobSwipe.webApp.service;

import com.JobSwipe.webApp.model.AuthenticationRequest;
import com.JobSwipe.webApp.model.AuthenticationResponse;
import com.JobSwipe.webApp.model.RegistrationRequest;
import com.JobSwipe.webApp.entities.UserConfig;
import com.JobSwipe.webApp.model.enums.Role;
//import com.JobSwipe.webApp.repository.AuthRequestRepository;
//import com.JobSwipe.webApp.repository.AuthResponseRepository;
//import com.JobSwipe.webApp.repository.RegistrationRepository;
import com.JobSwipe.webApp.repository.UserConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserConfigRepository userConfigRepository;
//    private final AuthRequestRepository authRequestRepository;
//    private final AuthResponseRepository authResponseRepository;
//    private final RegistrationRepository registrationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request) {
//        authRequestRepository.save(request);

        // TODO: in later stages we can check the token incoming in the header to check if
        //  the user still has a valid token and if yes we can short circuit.

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));
        log.info("Authentication Successful");

        UserConfig user = userConfigRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(user);

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .build();
//        authResponseRepository.save(authenticationResponse);
        return authenticationResponse;

    }

    public AuthenticationResponse registerUser(@RequestBody RegistrationRequest request) {
//        registrationRepository.save(request);

        UserConfig user = UserConfig.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .encryptedPassword(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userConfigRepository.save(user);
        log.info("User registered successfully");

        String token = jwtService.generateToken(user);
        log.info("Generated Token: {}", token);

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .build();
//        authResponseRepository.save(authenticationResponse);
        return authenticationResponse;
    }
}
