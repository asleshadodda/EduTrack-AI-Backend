
package com.edutrack.backend.controller;

import com.edutrack.backend.entity.Job;
import com.edutrack.backend.entity.Company;
import com.edutrack.backend.repository.JobRepository;
import com.edutrack.backend.repository.CompanyRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "http://localhost:5173")
public class JobController {

    private final JobRepository jobs;
    private final CompanyRepository companies;

    public JobController(JobRepository jobs, CompanyRepository companies) {
        this.jobs = jobs;
        this.companies = companies;
    }

    @GetMapping
    public List<Job> getAll() {
        return jobs.findAll();
    }

    @GetMapping("/active")
    public List<Job> getActive() {
        return jobs.findByStatus("ACTIVE");
    }

    @PostMapping
    public Job create(@RequestBody Job job) {
        if (job.getCompany() != null && job.getCompany().getId() != null) {
            Company company = companies.findById(job.getCompany().getId())
                .orElseThrow(() -> new RuntimeException("Company not found"));
            job.setCompany(company);
        }
        return jobs.save(job);
    }

    @PutMapping("/{id}")
    public Job update(@PathVariable Long id, @RequestBody Job updated) {
        Job job = jobs.findById(id)
            .orElseThrow(() -> new RuntimeException("Job not found"));

        job.setTitle(updated.getTitle());
        job.setDescription(updated.getDescription());
        job.setEligibilityCriteria(updated.getEligibilityCriteria());
        job.setSalary(updated.getSalary());
        job.setDeadline(updated.getDeadline());
        job.setStatus(updated.getStatus());

        if (updated.getCompany() != null && updated.getCompany().getId() != null) {
            Company company = companies.findById(updated.getCompany().getId())
                .orElseThrow(() -> new RuntimeException("Company not found"));
            job.setCompany(company);
        }

        return jobs.save(job);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        jobs.deleteById(id);
    }
}