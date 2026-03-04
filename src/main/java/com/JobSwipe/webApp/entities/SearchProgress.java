package com.JobSwipe.webApp.entities;

import com.JobSwipe.webApp.model.enums.SearchQueryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "search_progress")
public class SearchProgress {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    private String query;

    @Enumerated(EnumType.STRING)
    private SearchQueryStatus status;

    private LocalDateTime lastRun;
    private int pageStart;
    private int seedsDiscovered;
}