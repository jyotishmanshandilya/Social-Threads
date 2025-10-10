package com.socialThreads.webApp.repository;

import com.socialThreads.webApp.model.AuthenticationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthRequestRepository extends JpaRepository<AuthenticationRequest, UUID> {}
