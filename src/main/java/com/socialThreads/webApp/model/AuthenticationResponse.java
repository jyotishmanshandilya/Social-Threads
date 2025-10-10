package com.socialThreads.webApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "authentication_response")
public class AuthenticationResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String token;
    private LocalDateTime createdAt;
}
