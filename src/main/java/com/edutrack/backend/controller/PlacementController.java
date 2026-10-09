
package com.edutrack.backend.controller;

import com.edutrack.backend.entity.JobApplication;
import com.edutrack.backend.entity.Placement;
import com.edutrack.backend.repository.JobApplicationRepository;
import com.edutrack.backend.repository.PlacementRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/placements")
@CrossOrigin(origins = "http://localhost:5173")
public class PlacementController {

    private final PlacementRepository placementRepository;
    private final JobApplicationRepository applicationRepository;

    public PlacementController(
            PlacementRepository placementRepository,
            JobApplicationRepository applicationRepository) {
        this.placementRepository = placementRepository;
        this.applicationRepository = applicationRepository;
    }

    // Get all placement records
    @GetMapping
    public List<Placement> getAllPlacements() {
        return placementRepository.findAll();
    }

    // Get a placement by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPlacementById(@PathVariable Long id) {
        return placementRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a placement for a selected application
    @PostMapping
    public ResponseEntity<?> createPlacement(
            @RequestParam Long applicationId,
            @RequestParam Double packageAmount,
            @RequestParam(required = false) LocalDate placementDate) {

        if (applicationId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Application ID is required"));
        }

        if (packageAmount == null || packageAmount < 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Package amount cannot be negative"));
        }

        JobApplication application =
                applicationRepository.findById(applicationId).orElse(null);

        if (application == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Application not found"));
        }

        if (!"SELECTED".equalsIgnoreCase(application.getStatus())) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Only selected applications can receive placement records"));
        }

        if (placementRepository.existsByApplicationId(applicationId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message",
                            "A placement record already exists for this application"));
        }

        Placement placement = new Placement();
        placement.setApplication(application);
        placement.setOfferStatus("PENDING");
        placement.setPackageAmount(packageAmount);
        placement.setPlacementDate(
                placementDate != null ? placementDate : LocalDate.now());

        Placement savedPlacement = placementRepository.save(placement);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedPlacement);
    }

    // Update placement offer status
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updatePlacementStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Status is required"));
        }

        String normalizedStatus = status.trim().toUpperCase();

        List<String> allowedStatuses =
                List.of("PENDING", "ACCEPTED", "DECLINED");

        if (!allowedStatuses.contains(normalizedStatus)) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Status must be PENDING, ACCEPTED, or DECLINED"));
        }

        Placement placement =
                placementRepository.findById(id).orElse(null);

        if (placement == null) {
            return ResponseEntity.notFound().build();
        }

        placement.setOfferStatus(normalizedStatus);

        return ResponseEntity.ok(
                placementRepository.save(placement));
    }

    // Delete a placement
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlacement(@PathVariable Long id) {

        if (!placementRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        placementRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}