package tn.enicarthage.plate_be.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.plate_be.dtos.TechnicienDashboardDTO;
import tn.enicarthage.plate_be.services.TechnicienDashboardService;

@RestController
@RequestMapping("/api/technicien/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TechnicienDashboardController {

    private final TechnicienDashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ResponseEntity<TechnicienDashboardDTO> getDashboard(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(dashboardService.getDashboard(email));
    }
}