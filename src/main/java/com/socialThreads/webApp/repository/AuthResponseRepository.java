package com.socialThreads.webApp.repository;

import com.socialThreads.webApp.model.AuthenticationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthResponseRepository extends JpaRepository<AuthenticationResponse, UUID> {}
