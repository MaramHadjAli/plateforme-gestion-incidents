package tn.enicarthage.plate_be.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.plate_be.dtos.HistoriqueMaintenanceDTO;
import tn.enicarthage.plate_be.services.HistoriqueMaintenanceService;

import java.util.List;

@RestController
@RequestMapping("/api/historique-maintenance")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HistoriqueMaintenanceController {

    private final HistoriqueMaintenanceService historiqueService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIEN')")
    public ResponseEntity<List<HistoriqueMaintenanceDTO>> getAll() {
        return ResponseEntity.ok(historiqueService.getAll());
    }

    @GetMapping("/equipement/{idEquipement}")
    public ResponseEntity<List<HistoriqueMaintenanceDTO>> getByEquipement(@PathVariable String idEquipement) {
        return ResponseEntity.ok(historiqueService.getByEquipement(idEquipement));
    }

    @GetMapping("/technicien/{technicienId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<HistoriqueMaintenanceDTO>> getByTechnicien(@PathVariable Long technicienId) {
        return ResponseEntity.ok(historiqueService.getByTechnicien(technicienId));
    }
}
