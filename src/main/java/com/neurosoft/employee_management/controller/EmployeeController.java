package com.neurosoft.employee_management.controller;

import com.neurosoft.employee_management.entity.Employee;
import com.neurosoft.employee_management.repository.EmployeeRepository;
import com.neurosoft.employee_management.service.FileStorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository repository;
    private final FileStorageService fileStorageService;

    public EmployeeController(EmployeeRepository repository, FileStorageService fileStorageService) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public List<Employee> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Employee create(@RequestBody Employee employee) {
        return repository.save(employee);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Employee> updateStatus(@PathVariable Long id,
                                                 @RequestBody Employee statusUpdate) {
        return repository.findById(id).map(emp -> {
            emp.setStatus(statusUpdate.getStatus());
            return ResponseEntity.ok(repository.save(emp));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return repository.findById(id).map(emp -> {
            try {
                String url = fileStorageService.save(file, id);
                emp.setPhotoUrl(url);
                repository.save(emp);
                return ResponseEntity.ok(Map.of("photoUrl", url));
            } catch (Exception e) {
                return ResponseEntity.status(500)
                        .body(Map.of("error", "Upload failed: " + e.getMessage()));
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    // NEW: Voice Note Upload Endpoint
    @PostMapping(value = "/{id}/voice-note", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadVoiceNote(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return repository.findById(id).map(emp -> {
            try {
                String url = fileStorageService.save(file, id);
                emp.setVoiceNoteUrl(url);
                repository.save(emp);
                return ResponseEntity.ok(Map.of("voiceNoteUrl", url));
            } catch (Exception e) {
                return ResponseEntity.status(500)
                        .body(Map.of("error", "Upload failed: " + e.getMessage()));
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}