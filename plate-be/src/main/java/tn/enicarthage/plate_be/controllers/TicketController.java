package tn.enicarthage.plate_be.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.plate_be.dtos.TicketRequestDTO;
import tn.enicarthage.plate_be.dtos.TicketResponseDTO;
import tn.enicarthage.plate_be.services.PdfGenerationService;
import tn.enicarthage.plate_be.services.TicketService;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

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


        private final PdfGenerationService pdfGenerationService;

        @GetMapping("/{ticketId}/demande-prix/pdf")
        public ResponseEntity<InputStreamResource> generateDemandePrixPdf(@PathVariable String ticketId) {
            try {
                TicketResponseDTO ticket = ticketService.getTicketById(ticketId);

                List<TicketResponseDTO> tickets = List.of(ticket);

                int dpNumber = generateDpNumber();

                byte[] pdfBytes = pdfGenerationService.generateDemandePrixPdf(tickets, dpNumber);

                ByteArrayInputStream bis = new ByteArrayInputStream(pdfBytes);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION,
                        String.format("attachment; filename=demande_prix_%s_%s.pdf", ticketId, LocalDate.now()));

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(bis));

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        }

        // Alternative: Generate PDF for multiple tickets
        @PostMapping("/demande-prix/pdf")
        public ResponseEntity<InputStreamResource> generateDemandePrixPdfForMultiple(
                @RequestBody List<String> ticketIds) {
            try {
                List<TicketResponseDTO> tickets = ticketService.getTicketsByIds(ticketIds);
                int dpNumber = generateDpNumber();

                byte[] pdfBytes = pdfGenerationService.generateDemandePrixPdf(tickets, dpNumber);

                ByteArrayInputStream bis = new ByteArrayInputStream(pdfBytes);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION,
                        String.format("attachment; filename=demande_prix_DP-%02d_%s.pdf", dpNumber, LocalDate.now()));

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(bis));

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        }

        private int generateDpNumber() {
            return (int) (System.currentTimeMillis() % 10000);
        }
    }