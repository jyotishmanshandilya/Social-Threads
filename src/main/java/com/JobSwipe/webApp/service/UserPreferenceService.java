package com.JobSwipe.webApp.service;

import com.JobSwipe.webApp.entities.UserPreference;
import com.JobSwipe.webApp.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPreferenceService {
    private final UserPreferenceRepository repository;
    private final AuthService authService;

    public UserPreference create(UserPreference pref) {
        UUID userId = authService.getAuthenticatedUserId();

        log.debug("Creating preferences for user: {}", userId);

        pref.setId(UUID.randomUUID());
        pref.setUserId(userId);
        pref.setCreatedAt(LocalDateTime.now());
        pref.setUpdatedAt(LocalDateTime.now());

        log.info("Created preference for user: {}", pref.getUserId());
        return repository.save(pref);
    }

//    public UserPreference update(UUID id, UserPreference updated) {
//        UserPreference existing = repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Not found"));
//
//        existing.setPreferredJobTitles(updated.getPreferredJobTitles());
//        existing.setPreferredLocations(updated.getPreferredLocations());
//        existing.setEmploymentType(updated.getEmploymentType());
//        existing.setRemoteOk(updated.getRemoteOk());
//        existing.setWillingToRelocate(updated.getWillingToRelocate());
//        existing.setPreferredYoe(updated.getPreferredYoe());
//        existing.setUpdatedAt(LocalDateTime.now());
//        return repository.save(existing);
//    }
//
//    public void delete(UUID id) {
//        repository.deleteById(id);
//    }
}
