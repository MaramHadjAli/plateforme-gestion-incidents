package tn.enicarthage.plate_be.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.plate_be.dtos.EquipementDTO;
import tn.enicarthage.plate_be.dtos.EquipementRequestDTO;
import tn.enicarthage.plate_be.entities.ETAT_EQUIPEMENT;
import tn.enicarthage.plate_be.services.EquipementService;

import java.util.List;

@RestController
@RequestMapping("/api/equipements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EquipementController {

    private final EquipementService equipementService;

    @GetMapping
    public ResponseEntity<List<EquipementDTO>> getAll() {
        return ResponseEntity.ok(equipementService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipementDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(equipementService.getById(id));
    }

    @GetMapping("/salle/{idSalle}")
    public ResponseEntity<List<EquipementDTO>> getBySalle(@PathVariable String idSalle) {
        return ResponseEntity.ok(equipementService.getBySalle(idSalle));
    }

    @GetMapping("/garanties-expirees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EquipementDTO>> getGarantiesExpirees() {
        return ResponseEntity.ok(equipementService.getGarantiesExpirees());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipementDTO> create(@Valid @RequestBody EquipementRequestDTO dto) {
        return ResponseEntity.ok(equipementService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipementDTO> update(@PathVariable String id,
                                                @Valid @RequestBody EquipementRequestDTO dto) {
        return ResponseEntity.ok(equipementService.update(id, dto));
    }

    @PatchMapping("/{id}/etat")
    @PreAuthorize("hasAnyRole('ADMIN','TECHNICIEN')")
    public ResponseEntity<EquipementDTO> updateEtat(@PathVariable String id,
                                                    @RequestParam ETAT_EQUIPEMENT etat) {
        return ResponseEntity.ok(equipementService.updateEtat(id, etat));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        equipementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
