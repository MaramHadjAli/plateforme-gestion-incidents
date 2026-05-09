package tn.enicarthage.plate_be.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.plate_be.dtos.SalleDTO;
import tn.enicarthage.plate_be.dtos.SalleRequestDTO;
import tn.enicarthage.plate_be.services.SalleService;

import java.util.List;

@RestController
@RequestMapping("/api/salles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SalleController {

    private final SalleService salleService;

    @GetMapping
    public ResponseEntity<List<SalleDTO>> getAll() {
        return ResponseEntity.ok(salleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalleDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(salleService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalleDTO> create(@Valid @RequestBody SalleRequestDTO dto) {
        return ResponseEntity.ok(salleService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalleDTO> update(@PathVariable String id,
                                           @Valid @RequestBody SalleRequestDTO dto) {
        return ResponseEntity.ok(salleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        salleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
