package tn.enicarthage.plate_be.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.plate_be.dtos.TechnicienRankingDTO;
import tn.enicarthage.plate_be.dtos.TechnicianScoreDTO;
import tn.enicarthage.plate_be.services.ClassementService;

import java.util.List;

@RestController
@RequestMapping("/api/technicien")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClassementController {

    private final ClassementService classementService;

    @GetMapping("/ranking")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TechnicienRankingDTO>> getGlobalRanking() {
        return ResponseEntity.ok(classementService.getGlobalRanking());
    }

    @GetMapping("/my-score-details")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ResponseEntity<TechnicianScoreDTO> getMyScoreDetails(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(classementService.getTechnicianScoreDetails(email));
    }
}
