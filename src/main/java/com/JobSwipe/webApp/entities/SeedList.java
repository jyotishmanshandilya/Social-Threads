package com.JobSwipe.webApp.entities;

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
@Table(name = "seed_list")
public class SeedList {
    @Id
    private UUID id;
    private String company;
    private String jobBoard;
    private boolean validationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
