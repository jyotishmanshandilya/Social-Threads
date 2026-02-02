package com.JobSwipe.webApp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    private String firstName;
    private String lastName;

    @Id
    private String username;
    private String email;
    private String password;

    private LocalDateTime createdAt;
}
