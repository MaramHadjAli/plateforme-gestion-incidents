package tn.enicarthage.plate_be.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.plate_be.dtos.FeedbackDTO;
import tn.enicarthage.plate_be.services.FeedbackService;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/ticket/{ticketId}")
    @PreAuthorize("hasRole('DEMANDEUR')")
    public ResponseEntity<?> submitFeedback(@PathVariable String ticketId, @Valid @RequestBody FeedbackDTO feedbackDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        feedbackService.submitFeedback(ticketId, feedbackDTO, currentUserEmail);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Feedback submitted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/technician/{technicianId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getTechnicianFeedback(@PathVariable Long technicianId) {
        Map<String, Object> response = new HashMap<>();
        response.put("feedback", feedbackService.getTechnicianFeedback(technicianId));
        response.put("averageNote", feedbackService.getAverageNoteForTechnician(technicianId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ticket/{ticketId}/exists")
    @PreAuthorize("hasRole('DEMANDEUR')")
    public ResponseEntity<?> hasFeedback(@PathVariable String ticketId) {
        boolean exists = feedbackService.hasFeedback(ticketId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}
