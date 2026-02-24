package com.JobSwipe.webApp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_preferences")
public class UserPreference {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(columnDefinition = "uuid", nullable = false)
    private UUID userId;

    @Column(columnDefinition = "jsonb")
    private String preferredJobTitles; // Store as JSON String

    @Column(columnDefinition = "jsonb")
    private String preferredLocations; // Store as JSON String

    @Column(columnDefinition = "jsonb")
    private String employmentType;

    private Boolean remoteOk = false;
    private Boolean willingToRelocate = false;

    private String preferredYoe;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
