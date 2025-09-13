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
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private UUID id;

    private String street;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
