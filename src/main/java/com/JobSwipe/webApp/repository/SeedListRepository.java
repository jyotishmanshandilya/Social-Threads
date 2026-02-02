package com.JobSwipe.webApp.repository;

import com.JobSwipe.webApp.entities.SeedList;
import com.JobSwipe.webApp.model.CompanySeedIdDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeedListRepository extends JpaRepository<SeedList, UUID> {
    Optional<SeedList> findByCompany(String company);

    Boolean existsByCompany(String company);

    @Query(
            value = """
                SELECT company, id
                FROM public.seed_list
            """,
            nativeQuery = true
    )
    List<CompanySeedIdDTO> findDistinctCompany();
}
