package com.JobSwipe.webApp.controller;

import com.JobSwipe.webApp.model.AuthenticationRequest;
import com.JobSwipe.webApp.model.AuthenticationResponse;
import com.JobSwipe.webApp.model.RegistrationRequest;
import com.JobSwipe.webApp.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegistrationRequest registrationRequest) {
        AuthenticationResponse response = authService.registerUser(registrationRequest);
        log.info("User registered successfully: {}", response);
        if(response.getToken().isBlank()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody AuthenticationRequest authenticationRequest ) {
        AuthenticationResponse response = authService.authenticate(authenticationRequest);
        if(response.getToken().isBlank()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(response);
    }
}
