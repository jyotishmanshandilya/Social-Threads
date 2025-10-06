package com.socialThreads.webApp.repository;

import com.socialThreads.webApp.model.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserConfig, UUID> {
    Optional<UserConfig> findByUsername(String username);
}
