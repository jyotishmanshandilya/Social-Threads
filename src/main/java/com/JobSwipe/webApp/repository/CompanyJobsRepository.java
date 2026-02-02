package com.JobSwipe.webApp.repository;

import com.JobSwipe.webApp.entities.CompanyJobs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CompanyJobsRepository extends JpaRepository<CompanyJobs, UUID> {

    Boolean existsByJobId(String jobId);
}
