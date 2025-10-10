package com.socialThreads.webApp.repository;

import com.socialThreads.webApp.model.RegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RegistrationRepository extends JpaRepository<RegistrationRequest, UUID> {
}
