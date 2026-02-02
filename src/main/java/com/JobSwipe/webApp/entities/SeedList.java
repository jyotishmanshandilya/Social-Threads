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
    @Column(name = "id")
    private UUID id;

    @Column(name = "company", nullable = false)
    private String company;

    @Column(name = "job_board")
    private String jobBoard;

    @Column(name = "validation_status")
    private boolean validationStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
