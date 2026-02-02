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
@Table(name = "company_jobs")
public class CompanyJobs {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "seed_list_id")
    private UUID seedListId;

    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "application_url", nullable = false)
    private String applicationUrl;

    @Column(name = "job_id", nullable = false)
    private String jobId;

    @Column(name = "internal_job_id")
    private String internalJobId;

    @Column(name = "workplace_type")
    private String workplaceType;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "posted_date")
    private LocalDateTime postedDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "language")
    private String language;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
