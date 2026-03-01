package com.JobSwipe.webApp.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.json.JsonType;

import java.time.LocalDateTime;
import java.util.List;
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

    @Type(JsonType.class)
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "jsonb")
    private List<String> preferredJobTitles; // Store as JSON String

    @Type(JsonType.class)
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "jsonb")
    private List<String> preferredLocations; // Store as JSON String

    @Type(JsonType.class)
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "jsonb")
    private List<String> employmentType;

    private boolean remoteOk;
    private boolean willingToRelocate;

    private int yearsOfExperience;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
