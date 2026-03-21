package com.JobSwipe.webApp.service;

import com.JobSwipe.webApp.entities.CompanyJobs;
import com.JobSwipe.webApp.entities.UserPreference;
import com.JobSwipe.webApp.repository.UserPreferenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPreferenceServiceTest {

    @Mock private UserPreferenceRepository repository;
    @Mock private AuthService authService;

    @InjectMocks
    private UserPreferenceService userPreferenceService;

    // --- create ---

    @Test
    void create_setsUserIdFromAuthContext() {
        UUID userId = UUID.randomUUID();
        when(authService.getAuthenticatedUserId()).thenReturn(userId);

        UserPreference pref = new UserPreference();
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userPreferenceService.create(pref);

        assertThat(pref.getUserId()).isEqualTo(userId);
    }

    @Test
    void create_assignsNewId() {
        when(authService.getAuthenticatedUserId()).thenReturn(UUID.randomUUID());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserPreference pref = new UserPreference();
        userPreferenceService.create(pref);

        assertThat(pref.getId()).isNotNull();
    }

    @Test
    void create_setsCreatedAndUpdatedAt() {
        when(authService.getAuthenticatedUserId()).thenReturn(UUID.randomUUID());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserPreference pref = new UserPreference();
        userPreferenceService.create(pref);

        assertThat(pref.getCreatedAt()).isNotNull();
        assertThat(pref.getUpdatedAt()).isNotNull();
    }

    @Test
    void create_savesToRepository() {
        when(authService.getAuthenticatedUserId()).thenReturn(UUID.randomUUID());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserPreference pref = new UserPreference();
        userPreferenceService.create(pref);

        verify(repository).save(pref);
    }

    @Test
    void create_returnsPersistedPreference() {
        UUID userId = UUID.randomUUID();
        when(authService.getAuthenticatedUserId()).thenReturn(userId);

        UserPreference pref = new UserPreference();
        UserPreference saved = new UserPreference();
        saved.setId(UUID.randomUUID());
        when(repository.save(pref)).thenReturn(saved);

        UserPreference result = userPreferenceService.create(pref);
        assertThat(result).isSameAs(saved);
    }

    // --- filterJobs (currently a stub) ---

    @Test
    void filterJobs_returnsAllJobsUnchanged() {
        List<CompanyJobs> jobs = List.of(new CompanyJobs(), new CompanyJobs());
        List<CompanyJobs> result = userPreferenceService.filterJobs(jobs);
        assertThat(result).isSameAs(jobs);
    }

    @Test
    void filterJobs_returnsEmptyListForEmptyInput() {
        List<CompanyJobs> result = userPreferenceService.filterJobs(List.of());
        assertThat(result).isEmpty();
    }
}
