package tn.enicarthage.plate_be.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.plate_be.dtos.MaintenanceDTO;
import tn.enicarthage.plate_be.dtos.MaintenanceRequestDTO;
import tn.enicarthage.plate_be.entities.STATUS_MAINTENANCE;
import tn.enicarthage.plate_be.services.MaintenancePreventiveService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/maintenances")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MaintenanceController {

    private final MaintenancePreventiveService maintenanceService;

    @GetMapping
    public ResponseEntity<List<MaintenanceDTO>> getAll() {
        return ResponseEntity.ok(maintenanceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceService.getById(id));
    }

    @GetMapping("/equipement/{idEquipement}")
    public ResponseEntity<List<MaintenanceDTO>> getByEquipement(@PathVariable String idEquipement) {
        return ResponseEntity.ok(maintenanceService.getByEquipement(idEquipement));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIEN')")
    public ResponseEntity<List<MaintenanceDTO>> getOverdue() {
        return ResponseEntity.ok(maintenanceService.getOverdue());
    }

    @GetMapping("/range")
    public ResponseEntity<List<MaintenanceDTO>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date end) {
        return ResponseEntity.ok(maintenanceService.getByDateRange(start, end));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIEN')")
    public ResponseEntity<MaintenanceDTO> create(@Valid @RequestBody MaintenanceRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(maintenanceService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIEN')")
    public ResponseEntity<MaintenanceDTO> update(@PathVariable Long id,
                                                 @Valid @RequestBody MaintenanceRequestDTO dto) {
        return ResponseEntity.ok(maintenanceService.update(id, dto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIEN')")
    public ResponseEntity<MaintenanceDTO> updateStatus(@PathVariable Long id,
                                                       @RequestParam STATUS_MAINTENANCE status) {
        return ResponseEntity.ok(maintenanceService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        maintenanceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
