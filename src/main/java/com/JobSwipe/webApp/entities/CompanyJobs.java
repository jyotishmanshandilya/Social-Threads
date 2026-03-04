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
    private UUID id;
    private UUID seedListId;
    private String jobTitle;
    private String location;
    private String applicationUrl;
    private String jobId;
    private String internalJobId;
    private String workplaceType;
    private Integer yearsOfExperience;
    private LocalDateTime postedDate;
    private LocalDateTime createdAt;
    private String language;
    private LocalDateTime updatedAt;
}
