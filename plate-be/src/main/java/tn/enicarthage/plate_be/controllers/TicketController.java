package tn.enicarthage.plate_be.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.plate_be.dtos.*;
import tn.enicarthage.plate_be.entities.STATUS_TICKET;
import tn.enicarthage.plate_be.services.TicketService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponseDTO> createTicket(@RequestBody TicketRequestDTO ticketRequestDTO) {
        TicketResponseDTO createdTicket = ticketService.createTicket(ticketRequestDTO);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> getAllTickets() {
        List<TicketResponseDTO> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> getTicketById(@PathVariable String id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TicketResponseDTO>> getMyTickets(Authentication authentication) {
        String email = authentication.getName();
        boolean isTechnicien = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TECHNICIEN") || a.getAuthority().equals("TECHNICIEN"));
        
        List<TicketResponseDTO> tickets;
        if (isTechnicien) {
            try {
                tickets = ticketService.getTicketsByTechnicien(email);
            } catch (Exception e) {
                tickets = List.of();
            }
        } else {
            tickets = ticketService.getTicketsByDemandeur(email);
        }
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponseDTO> assignTicket(
            @PathVariable String id,
            @RequestBody Map<String, Long> body) {
        Long technicienId = body.get("technicienId");
        return ResponseEntity.ok(ticketService.assignTicket(id, technicienId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TicketResponseDTO> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        STATUS_TICKET newStatus = STATUS_TICKET.valueOf(body.get("status"));
        return ResponseEntity.ok(ticketService.updateStatus(id, newStatus));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTicket(@PathVariable String id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/close")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ResponseEntity<TicketResponseDTO> closeTicket(@PathVariable String id) {
        return ResponseEntity.ok(ticketService.closeTicket(id));
    }

    @PostMapping("/{id}/send-demande-prix")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DemandePrixResponse> sendDemandePrix(
            @PathVariable String id,
            @Valid @RequestBody DemandePrixRequest request) {
        DemandePrixResponse response = ticketService.sendDemandePrix(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/interest")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ResponseEntity<?> expressInterest(
            @PathVariable String id,
            @Valid @RequestBody TechnicianInterestRequest request) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        ticketService.technicianInterest(id, request, email);
        return ResponseEntity.ok(Map.of("message", "Votre réponse a été enregistrée"));
    }

    @GetMapping("/{id}/interested-technicians")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InterestedTechnicianDTO>> getInterestedTechnicians(@PathVariable String id) {
        List<InterestedTechnicianDTO> technicians = ticketService.getInterestedTechnicians(id);
        return ResponseEntity.ok(technicians);
    }

    @GetMapping("/{id}/has-responded")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ResponseEntity<?> hasResponded(@PathVariable String id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        boolean hasResponded = ticketService.hasTechnicianResponded(id, email);
        return ResponseEntity.ok(Map.of("hasResponded", hasResponded));
    }
}