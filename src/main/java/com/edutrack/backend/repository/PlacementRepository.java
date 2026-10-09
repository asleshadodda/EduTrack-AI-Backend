package com.edutrack.backend.repository;

import com.edutrack.backend.entity.Placement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlacementRepository extends JpaRepository<Placement, Long> {

    long countByOfferStatus(String offerStatus);

    boolean existsByApplicationId(Long applicationId);
}