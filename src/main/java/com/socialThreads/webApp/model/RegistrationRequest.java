package com.socialThreads.webApp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "registration_request")
public class RegistrationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID registrationId;

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;

    private LocalDateTime createdAt;
}
