
package com.edutrack.backend.controller;

import com.edutrack.backend.entity.Company;
import com.edutrack.backend.repository.CompanyRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "http://localhost:5173")
public class CompanyController {

    private final CompanyRepository repository;

    public CompanyController(CompanyRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Company> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Company create(@RequestBody Company company) {
        return repository.save(company);
    }

    @PutMapping("/{id}")
    public Company update(@PathVariable Long id, @RequestBody Company updated) {
        Company company = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Company not found"));

        company.setName(updated.getName());
        company.setIndustry(updated.getIndustry());
        company.setLocation(updated.getLocation());
        company.setWebsite(updated.getWebsite());
        company.setContactEmail(updated.getContactEmail());

        return repository.save(company);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}