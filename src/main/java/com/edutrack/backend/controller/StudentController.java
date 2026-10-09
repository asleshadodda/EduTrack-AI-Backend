
package com.edutrack.backend.controller;

import com.edutrack.backend.entity.Student;
import com.edutrack.backend.repository.StudentRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:5173")
public class StudentController {

    private final StudentRepository repository;

    public StudentController(StudentRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Student> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Student getById(@PathVariable Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    @PostMapping
    public Student create(@RequestBody Student student) {
        return repository.save(student);
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @RequestBody Student updated) {
        Student student = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setFullName(updated.getFullName());
        student.setEmail(updated.getEmail());
        student.setPhone(updated.getPhone());
        student.setDepartment(updated.getDepartment());
        student.setGraduationYear(updated.getGraduationYear());
        student.setCgpa(updated.getCgpa());

        return repository.save(student);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}