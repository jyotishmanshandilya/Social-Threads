package com.JobSwipe.webApp.repository;

import com.JobSwipe.webApp.entities.SearchProgress;
import com.JobSwipe.webApp.model.enums.SearchQueryStatus;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SearchProgressRepository extends JpaRepository<SearchProgress, UUID> {

    Optional<SearchProgress> findTopByQueryAndStatusOrderByLastRunDesc(String query, SearchQueryStatus status);
}
