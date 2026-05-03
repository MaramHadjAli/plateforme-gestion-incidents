package tn.enicarthage.plate_be.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.plate_be.dtos.PointTransactionDTO;
import tn.enicarthage.plate_be.dtos.TechnicianRankDTO;
import tn.enicarthage.plate_be.dtos.TechnicienDashboardDTO;
import tn.enicarthage.plate_be.dtos.TechnicianScoreDTO;
import tn.enicarthage.plate_be.services.TechnicienDashboardService;
import tn.enicarthage.plate_be.services.TechnicienService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/technicien")
@PreAuthorize("hasRole('TECHNICIEN') or hasRole('ADMIN')")
public class TechnicienDashboardController {

    private final TechnicienDashboardService technicienDashboardService;
    private final TechnicienService technicienService;

    public TechnicienDashboardController(TechnicienDashboardService technicienDashboardService, TechnicienService technicienService) {
        this.technicienDashboardService = technicienDashboardService;
        this.technicienService = technicienService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<TechnicienDashboardDTO> getDashboard() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        TechnicienDashboardDTO dashboard = technicienDashboardService.getDashboard(email);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/my-score")
    public ResponseEntity<?> getMyScore() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        TechnicienDashboardDTO dashboard = technicienDashboardService.getDashboard(email);

        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/my-ranking")
    public ResponseEntity<?> getMyRanking() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        TechnicienDashboardDTO dashboard = technicienDashboardService.getDashboard(email);

        List<TechnicianRankDTO> ranking = technicienService.getRanking();
        int rank = 0;
        for (int i = 0; i < ranking.size(); i++) {
            if (ranking.get(i).getEmail().equals(email)) {
                rank = i + 1;
                break;
            }
        }

        return ResponseEntity.ok(Map.of("rank", rank, "totalTechnicians", ranking.size()));
    }

    @GetMapping("/my-transactions")
    public ResponseEntity<?> getMyTransactions() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        TechnicienDashboardDTO dashboard = technicienDashboardService.getDashboard(email);

        Long technicianId = dashboard.getId();
        List<PointTransactionDTO> transactions = technicienService.getTechnicianTransactions(technicianId);

        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/my-score-details")
    public ResponseEntity<?> getMyScoreDetails() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        TechnicienDashboardDTO dashboard = technicienDashboardService.getDashboard(email);

        TechnicianScoreDTO scoreDetails = technicienService.getTechnicianScoreWithDetails(dashboard.getId());

        return ResponseEntity.ok(scoreDetails);
    }

    @GetMapping("/ranking")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getTechnicianRanking() {
        List<TechnicianRankDTO> ranking = technicienService.getRanking();
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/{id}/score-details")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIEN')")
    public ResponseEntity<?> getTechnicianScoreDetails(@PathVariable Long id) {
        TechnicianScoreDTO scoreDetails = technicienService.getTechnicianScoreWithDetails(id);
        return ResponseEntity.ok(scoreDetails);
    }

    @GetMapping("/{id}/transactions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIEN')")
    public ResponseEntity<?> getTechnicianTransactions(@PathVariable Long id) {
        List<PointTransactionDTO> transactions = technicienService.getTechnicianTransactions(id);
        return ResponseEntity.ok(transactions);
    }
}