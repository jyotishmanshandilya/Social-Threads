package com.JobSwipe.webApp.repository;

import com.JobSwipe.webApp.entities.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserConfigRepository extends JpaRepository<UserConfig, UUID> {
    Optional<UserConfig> findByUsername(String username);
}
