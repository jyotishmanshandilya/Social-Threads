package com.JobSwipe.webApp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "job_department_mapping")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobDepartmentMapping {
    @Id
    private UUID id;
    private String jobId;
    private String department;
    private String departmentId;
}
