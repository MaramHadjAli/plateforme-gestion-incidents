package tn.enicarthage.plate_be.controllers;

import tn.enicarthage.plate_be.annotations.Loggable;
import tn.enicarthage.plate_be.dtos.*;
import tn.enicarthage.plate_be.entities.PointTransaction;
import tn.enicarthage.plate_be.entities.ROLE;
import tn.enicarthage.plate_be.entities.Technicien;
import tn.enicarthage.plate_be.entities.Utilisateur;
import tn.enicarthage.plate_be.repositories.PointTransactionRepository;
import tn.enicarthage.plate_be.repositories.TechnicienRepository;
import tn.enicarthage.plate_be.services.BadgeService;
import tn.enicarthage.plate_be.services.TechnicienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/techniciens")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTechnicienController {

    @Autowired
    private TechnicienService technicienService;

    @Autowired
    private TechnicienRepository technicienRepository;

    @Autowired
    private PointTransactionRepository pointTransactionRepository;

    @Autowired
    private BadgeService badgeService;

    @GetMapping
    @PreAuthorize("permitAll()")
    @Loggable(action = "GET_ALL_TECHNICIENS_API", description = "API endpoint to get all active technicians")
    public ResponseEntity<List<TechnicienResponseDTO>> getAllTechniciens() {
        List<TechnicienResponseDTO> techniciens = technicienService.getAllTechniciens();
        return ResponseEntity.ok(techniciens);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    @Loggable(action = "GET_TECHNICIEN_BY_ID_API", description = "API endpoint to get technician by ID")
    public ResponseEntity<TechnicienResponseDTO> getTechnicienById(@PathVariable Long id) throws Exception {
        TechnicienResponseDTO technicien = technicienService.getTechnicienById(id);
        return ResponseEntity.ok(technicien);
    }

    @PostMapping
    @Loggable(action = "CREATE_TECHNICIEN_API", description = "API endpoint to create new technician")
    public ResponseEntity<Map<String, Object>> createTechnicien(@Valid @RequestBody NewTechnicienDTO dto) {
        TechnicienResponseDTO newTechnicien = technicienService.createTechnicien(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Technicien created successfully");
        response.put("technicien", newTechnicien);
        response.put("technicienId", newTechnicien.getIdUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/desactivate")
    @Loggable(action = "DESACTIVATE_TECHNICIEN_API", description = "API endpoint to soft delete technician")
    public ResponseEntity<Map<String, Object>> desactivateTechnicien(
            @PathVariable Long id,
            @Valid @RequestBody TechnicienDesactivationDTO dto) throws Exception {

        technicienService.desactivateTechnicien(id, dto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Technicien desactivated successfully");
        response.put("technicienId", id);
        response.put("dateDesactivation", java.time.LocalDate.now().toString());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/confidential-reason")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Loggable(action = "GET_CONFIDENTIAL_REASON_API", description = "API endpoint to get confidential desactivation reason")
    public ResponseEntity<Map<String, String>> getConfidentialReason(@PathVariable Long id) throws Exception {
        String reason = technicienService.getConfidentialReason(id);
        Map<String, String> response = new HashMap<>();
        response.put("confidentialReason", reason);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reactivate")
    @Loggable(action = "REACTIVATE_TECHNICIEN_API", description = "API endpoint to reactivate technician")
    public ResponseEntity<Map<String, String>> reactivateTechnicien(@PathVariable Long id) throws Exception {
        technicienService.reactivateTechnicien(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Technicien reactivated successfully");
        response.put("technicienId", id.toString());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/score")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIEN')")
    public ResponseEntity<?> getTechnicianScore(@PathVariable Long id) {
        Optional<Technicien> technicienOpt = technicienService.findById(id);

        if (technicienOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Technicien not found"));
        }

        Technicien technicien = technicienOpt.get();

        Map<String, Object> response = new HashMap<>();
        response.put("totalPoints", technicien.getTotalPoints());
        response.put("averageNote", technicien.getNoteMoyenne() != null ? technicien.getNoteMoyenne() : 0.0);
        response.put("technicienId", id);
        response.put("technicienNom", technicien.getNom());
        response.put("technicienPrenom", technicien.getPrenom());
        response.put("specialite", technicien.getSpecialite());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/leaderboard")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getTechnicianLeaderboard() {
        List<Technicien> techniciens = technicienService.findAllTechniciens();

        List<Map<String, Object>> leaderboard = techniciens.stream()
                .filter(tech -> tech.isActive())
                .map(tech -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("id", tech.getId());
                    entry.put("nom", tech.getNom());
                    entry.put("prenom", tech.getPrenom());
                    entry.put("email", tech.getEmail());
                    entry.put("specialite", tech.getSpecialite());
                    entry.put("totalPoints",tech.getTotalPoints());
                    entry.put("averageNote", tech.getNoteMoyenne() != null ? tech.getNoteMoyenne() : 0.0);
                    int points = tech.getTotalPoints();
                    entry.put("badge", badgeService.getBadgeByPoints(points));
                    return entry;
                })
                .sorted((a, b) -> Integer.compare(
                        (int) b.get("totalPoints"),
                        (int) a.get("totalPoints")
                ))
                .collect(Collectors.toList());

        for (int i = 0; i < leaderboard.size(); i++) {
            leaderboard.get(i).put("rank", i + 1);
        }

        return ResponseEntity.ok(leaderboard);
    }
}