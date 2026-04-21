package tn.enicarthage.plate_be.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.plate_be.dtos.TicketRequestDTO;
import tn.enicarthage.plate_be.dtos.TicketResponseDTO;
import tn.enicarthage.plate_be.services.TicketService;
import tn.enicarthage.plate_be.services.pdfGenerationImpl;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class TicketController {

    @Autowired
    private final TicketService ticketService;
    @Autowired
    private final pdfGenerationImpl pdfGenerationService;

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

    /**
     * PDF pour TOUS les tickets ouverts.
     * GET /api/tickets/demande-prix/pdf?dpNumber=5
     */
    @GetMapping("/demande-prix/pdf")
    public ResponseEntity<byte[]> downloadDemandePrixAll(
            @RequestParam(value = "dpNumber", defaultValue = "5") int dpNumber) {
        try {
            List<TicketResponseDTO> tickets = ticketService.getAllTickets();
            return buildPdfResponse(tickets, dpNumber);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * PDF pour UN ticket spécifique (panne signalée).
     * GET /api/tickets/{id}/demande-prix/pdf?dpNumber=5
     */
    @GetMapping("/{id}/demande-prix/pdf")
    public ResponseEntity<byte[]> downloadDemandePrixForTicket(
            @PathVariable String id,
            @RequestParam(value = "dpNumber", defaultValue = "5") int dpNumber) {
        try {
            TicketResponseDTO ticket = ticketService.getTicketById(id);
            return buildPdfResponse(List.of(ticket), dpNumber);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * PDF pour une SÉLECTION de tickets.
     * POST /api/tickets/demande-prix/pdf
     * Body: { "ticketIds": ["T001", "T002"], "dpNumber": 5 }
     */
    @PostMapping("/demande-prix/pdf")
    public ResponseEntity<byte[]> downloadDemandePrixSelection(
            @RequestBody DemandePrixRequest request) {
        try {
            List<TicketResponseDTO> tickets = ticketService.getTicketsByIds(request.getTicketIds());
            return buildPdfResponse(tickets, request.getDpNumber());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<byte[]> buildPdfResponse(List<TicketResponseDTO> tickets, int dpNumber)
            throws IOException {
        byte[] pdfBytes = pdfGenerationService.generateDemandePrixPdf(tickets, dpNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // "inline" = s'ouvre dans le navigateur ; mettre "attachment" pour forcer le téléchargement
        headers.setContentDispositionFormData("inline",
                String.format("demande_de_prix_DP-%02d.pdf", dpNumber));
        headers.setContentLength(pdfBytes.length);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    public static class DemandePrixRequest {
        private List<String> ticketIds;
        private int dpNumber = 5;
        public List<String> getTicketIds() { return ticketIds; }
        public void setTicketIds(List<String> ids) { this.ticketIds = ids; }
        public int getDpNumber() { return dpNumber; }
        public void setDpNumber(int n) { this.dpNumber = n; }
    }
}