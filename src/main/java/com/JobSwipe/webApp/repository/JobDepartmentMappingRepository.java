package com.JobSwipe.webApp.repository;

import com.JobSwipe.webApp.entities.JobDepartmentMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JobDepartmentMappingRepository extends JpaRepository<JobDepartmentMapping, UUID> {

    Boolean existsByJobId(String jobId);
}
