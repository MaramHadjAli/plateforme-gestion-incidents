package tn.enicarthage.plate_be.services;

import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.dtos.FeedbackDTO;
import tn.enicarthage.plate_be.entities.Demandeur;
import tn.enicarthage.plate_be.entities.Feedback;
import tn.enicarthage.plate_be.entities.STATUS_TICKET;
import tn.enicarthage.plate_be.entities.Ticket;
import tn.enicarthage.plate_be.repositories.DemandeurRepository;
import tn.enicarthage.plate_be.repositories.FeedbackRepository;
import tn.enicarthage.plate_be.repositories.TicketRepository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final TicketRepository ticketRepository;
    private final DemandeurRepository demandeurRepository;
    private final ScoreCalculatorService scoreCalculatorService;

    public FeedbackService(FeedbackRepository feedbackRepository,
                           TicketRepository ticketRepository,
                           DemandeurRepository demandeurRepository,
                           ScoreCalculatorService scoreCalculatorService) {
        this.feedbackRepository = feedbackRepository;
        this.ticketRepository = ticketRepository;
        this.demandeurRepository = demandeurRepository;
        this.scoreCalculatorService = scoreCalculatorService;
    }

    public void submitFeedback(String ticketId, FeedbackDTO feedbackDTO, String demandeurEmail) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));


        Demandeur demandeur = demandeurRepository.findByEmail(demandeurEmail);
        if (demandeur == null) {
            throw new RuntimeException("Demandeur not found");
        }

        Feedback feedback = new Feedback();
        feedback.setNote(feedbackDTO.getNote());
        feedback.setCommentaire(feedbackDTO.getCommentaire());
        feedback.setDateEvaluation(new Date());
        feedback.setTicket(ticket);
        feedback.setDemandeur(demandeur);

        feedbackRepository.save(feedback);

        if (ticket.getAssignedTo() != null) {
            scoreCalculatorService.updateTechnicianScore(ticket.getAssignedTo().getId());
        }
    }

    public List<Map<String, Object>> getTechnicianFeedback(Long technicianId) {
        List<Feedback> feedbacks = feedbackRepository.findByTechnicienId(technicianId);

        return feedbacks.stream().map(feedback -> {
            Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", feedback.getIdFeedback());
            item.put("note", feedback.getNote());
            item.put("commentaire", feedback.getCommentaire());
            item.put("dateEvaluation", feedback.getDateEvaluation());
            item.put("ticketId", feedback.getTicket().getIdTicket());
            item.put("ticketTitle", feedback.getTicket().getTitre());
            item.put("demandeurNom", feedback.getDemandeur().getNom());
            item.put("demandeurPrenom", feedback.getDemandeur().getPrenom());
            return item;
        }).collect(Collectors.toList());
    }

    public double getAverageNoteForTechnician(Long technicianId) {
        Double average = feedbackRepository.getAverageNoteByTechnicienId(technicianId);
        return average != null ? average : 0.0;
    }

    public boolean hasFeedback(String ticketId) {
        return feedbackRepository.existsByTicketId(ticketId);
    }
}