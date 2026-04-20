package tn.enicarthage.plate_be.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.enicarthage.plate_be.dtos.TicketCreateRequest;
import tn.enicarthage.plate_be.dtos.TicketCreateResponse;
import tn.enicarthage.plate_be.entities.Ticket;
import tn.enicarthage.plate_be.services.TicketService;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TicketCreateResponse> createTicket(
            @RequestPart("data") TicketCreateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return ResponseEntity.ok(ticketService.createTicket(request, file));
    }

    /**
     * Récupérer les tickets de l'utilisateur actuel
     */
    @GetMapping("/my-tickets")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Ticket>> getMyTickets() {
        return ResponseEntity.ok(ticketService.getTicketsByCurrentUser());
    }
}

