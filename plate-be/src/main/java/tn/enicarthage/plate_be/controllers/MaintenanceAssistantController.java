package tn.enicarthage.plate_be.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.plate_be.services.GeminiService;
import tn.enicarthage.plate_be.services.MaintenancePreventiveService;
import tn.enicarthage.plate_be.dtos.MaintenanceDTO;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai/maintenance")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MaintenanceAssistantController {

    private final GeminiService geminiService;
    private final MaintenancePreventiveService maintenanceService;
    private final tn.enicarthage.plate_be.services.TicketService ticketService;

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        String prompt = request.getPrompt();


        if (request.getEquipmentId() != null) {
            List<MaintenanceDTO> history = maintenanceService.getByEquipement(request.getEquipmentId());
            String context = "Voici l'historique de maintenance pour l'équipement " + request.getEquipmentId() + ":\n";
            context += history.stream()
                    .map(m -> "- Date: " + m.getDateProgramme() + ", Status: " + m.getStatus() + ", Description: " + m.getDescription())
                    .collect(Collectors.joining("\n"));
            prompt = context + "\n\nQuestion de l'utilisateur: " + prompt;
        }

        String response = geminiService.generateContent(prompt);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/suggest/{equipmentId}")
    public ResponseEntity<String> suggestNextMaintenance(@PathVariable String equipmentId) {
        List<MaintenanceDTO> history = maintenanceService.getByEquipement(equipmentId);
        String prompt = "En te basant sur l'historique suivant d'un équipement, suggère la prochaine date de maintenance préventive et explique pourquoi:\n";
        prompt += history.stream()
                .map(m -> "- Date: " + m.getDateProgramme() + ", Status: " + m.getStatus() + ", Description: " + m.getDescription())
                .collect(Collectors.joining("\n"));

        String response = geminiService.generateContent(prompt);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/prevent/{ticketId}")
    public ResponseEntity<String> getPreventionAdvice(@PathVariable String ticketId) {
        tn.enicarthage.plate_be.dtos.TicketResponseDTO ticket = ticketService.getTicketById(ticketId);

        String prompt = "En te basant sur l'incident suivant :\n" +
                "Titre: " + ticket.getTitre() + "\n" +
                "Description: " + ticket.getDescription() + "\n" +
                (ticket.getNomEquipement() != null ? "Équipement concerné: " + ticket.getNomEquipement() + "\n" : "") +
                "Suggère des mesures concrètes de maintenance préventive pour éviter que ce type d'incident ne se reproduise à l'avenir. " +
                "Réponds en français, avec un ton professionnel et structuré.";

        String response = geminiService.generateContent(prompt);
        return ResponseEntity.ok(response);
    }

    @lombok.Data
    public static class ChatRequest {
        private String prompt;
        private String equipmentId;
    }
}
