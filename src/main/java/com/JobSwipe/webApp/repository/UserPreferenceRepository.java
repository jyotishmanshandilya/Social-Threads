package com.JobSwipe.webApp.repository;


import com.JobSwipe.webApp.entities.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, UUID> {
    // Custom queries if needed
}
