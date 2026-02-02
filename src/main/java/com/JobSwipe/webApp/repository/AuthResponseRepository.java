package com.JobSwipe.webApp.repository;

import com.JobSwipe.webApp.model.AuthenticationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthResponseRepository extends JpaRepository<AuthenticationResponse, UUID> {}
