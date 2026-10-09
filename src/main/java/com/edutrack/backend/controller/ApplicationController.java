
package com.edutrack.backend.controller;

import com.edutrack.backend.entity.Job;
import com.edutrack.backend.entity.JobApplication;
import com.edutrack.backend.entity.Student;
import com.edutrack.backend.repository.JobApplicationRepository;
import com.edutrack.backend.repository.JobRepository;
import com.edutrack.backend.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "http://localhost:5173")
public class ApplicationController {

    private final JobApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;

    public ApplicationController(
            JobApplicationRepository applicationRepository,
            StudentRepository studentRepository,
            JobRepository jobRepository) {
        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
    }

    @GetMapping
    public List<JobApplication> getAllApplications() {
        return applicationRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getApplicationById(@PathVariable Long id) {
        return applicationRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getApplicationsByStudent(
            @PathVariable Long studentId) {

        if (!studentRepository.existsById(studentId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                applicationRepository.findByStudentId(studentId));
    }

    @PostMapping
    public ResponseEntity<?> applyForJob(
            @RequestParam Long studentId,
            @RequestParam Long jobId) {

        Student student = studentRepository.findById(studentId)
                .orElse(null);

        if (student == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Student not found"));
        }

        Job job = jobRepository.findById(jobId).orElse(null);

        if (job == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Job not found"));
        }

        if (!"ACTIVE".equalsIgnoreCase(job.getStatus())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "This job is not active"));
        }

        boolean alreadyApplied = applicationRepository.findAll()
                .stream()
                .anyMatch(application ->
                        application.getStudent().getId().equals(studentId)
                        && application.getJob().getId().equals(jobId));

        if (alreadyApplied) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message",
                            "This student has already applied for this job"));
        }

        JobApplication application = new JobApplication();
        application.setStudent(student);
        application.setJob(job);
        application.setAppliedDate(LocalDate.now());
        application.setStatus("APPLIED");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationRepository.save(application));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        List<String> allowedStatuses = List.of(
                "APPLIED", "SHORTLISTED", "SELECTED", "REJECTED");

        String normalizedStatus = status.trim().toUpperCase();

        if (!allowedStatuses.contains(normalizedStatus)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Status must be APPLIED, SHORTLISTED, SELECTED, or REJECTED"));
        }

        JobApplication application =
                applicationRepository.findById(id).orElse(null);

        if (application == null) {
            return ResponseEntity.notFound().build();
        }

        application.setStatus(normalizedStatus);

        return ResponseEntity.ok(
                applicationRepository.save(application));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApplication(@PathVariable Long id) {
        if (!applicationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        applicationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}