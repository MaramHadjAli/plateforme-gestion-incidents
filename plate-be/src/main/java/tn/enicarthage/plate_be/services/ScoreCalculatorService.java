package tn.enicarthage.plate_be.services;

import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.entities.*;
import tn.enicarthage.plate_be.repositories.BadgeRepository;
import tn.enicarthage.plate_be.repositories.FeedbackRepository;
import tn.enicarthage.plate_be.repositories.PointTransactionRepository;
import tn.enicarthage.plate_be.repositories.TechnicienRepository;
import tn.enicarthage.plate_be.services.BadgeService;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ScoreCalculatorService {

    private final PointTransactionRepository pointTransactionRepository;
    private final FeedbackRepository feedbackRepository;
    private final TechnicienRepository technicienRepository;
    private final BadgeService badgeService;
    private final BadgeRepository badgeRepository;

    public ScoreCalculatorService(PointTransactionRepository pointTransactionRepository,
                                  FeedbackRepository feedbackRepository,
                                  TechnicienRepository technicienRepository,
                                  BadgeService badgeService,
                                  BadgeRepository badgeRepository) {
        this.pointTransactionRepository = pointTransactionRepository;
        this.feedbackRepository = feedbackRepository;
        this.technicienRepository = technicienRepository;
        this.badgeService = badgeService;
        this.badgeRepository = badgeRepository;
    }

    public int calculateScore(Ticket ticket, Technicien technicien) {
        int baseScore = calculateBaseScore(ticket.getPriorite());
        double timeMultiplier = calculateTimeMultiplier(ticket);
        int satisfactionPoints = getSatisfactionPoints(ticket);
        int complexityBonus = calculateComplexityBonus(ticket);

        int timeScore = (int)(baseScore * timeMultiplier);
        int totalScore = (timeScore + satisfactionPoints + complexityBonus) / 2;

        saveScoreTransaction(ticket, technicien, totalScore, baseScore, timeMultiplier, satisfactionPoints, complexityBonus);

        return totalScore;
    }

    private int calculateBaseScore(PRIORITE_TICKET priorite) {
        switch (priorite) {
            case CRITIQUE:
                return 100;
            case HAUTE:
                return 60;
            case NORMALE:
                return 30;
            case FAIBLE:
                return 10;
            default:
                return 20;
        }
    }

    private double calculateTimeMultiplier(Ticket ticket) {
        if (ticket.getDateCreation() == null || ticket.getDateCloture() == null) {
            return 1.0;
        }

        long diffInMillies = Math.abs(ticket.getDateCloture().getTime() - ticket.getDateCreation().getTime());
        long diffInHours = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        if (diffInHours < 1) {
            return 2.0;
        } else if (diffInHours <= 4) {
            return 1.5;
        } else if (diffInHours <= 24) {
            return 1.0;
        } else if (diffInHours <= 72) {
            return 0.7;
        } else if (diffInHours <= 168) {
            return 0.4;
        } else {
            return 0.2;
        }
    }

    private int getSatisfactionPoints(Ticket ticket) {
        java.util.Optional<Feedback> feedbackOpt = feedbackRepository.findByTicketIdTicket(ticket.getIdTicket());
        if (!feedbackOpt.isPresent()) {
            return 0;
        }

        Feedback feedback = feedbackOpt.get();
        switch (feedback.getNote()) {
            case 5:
                return 20;
            case 4:
                return 10;
            case 3:
                return 0;
            case 2:
                return -10;
            case 1:
                return -20;
            default:
                return 0;
        }
    }

    private int calculateComplexityBonus(Ticket ticket) {
        if (ticket.getEquipement() != null) {
            String equipementType = ticket.getEquipement().getType();
            if (equipementType != null) {
                if (equipementType.equalsIgnoreCase("CRITIQUE") || equipementType.equalsIgnoreCase("SERVER")) {
                    return 30;
                } else if (equipementType.equalsIgnoreCase("COMPLEXE") || equipementType.equalsIgnoreCase("NETWORK")) {
                    return 15;
                }
            }
        }
        return 0;
    }

    private void saveScoreTransaction(Ticket ticket, Technicien technicien, int totalScore,
                                      int baseScore, double timeMultiplier, int satisfactionPoints, int complexityBonus) {
        System.out.println("=== SAVING SCORE TRANSACTION ===");
        System.out.println("Technicien ID: " + technicien.getId());
        System.out.println("Total Score: " + totalScore);

        PointTransaction transaction = PointTransaction.builder()
                .quantite(totalScore)
                .dateAttribution(new Date())
                .raison(String.format(
                        "Ticket %s - Priorité: %s, Score base: %d, Temps: %.1fx, Satisfaction: %d, Complexité: %d | Total: %d",
                        ticket.getIdTicket(),
                        ticket.getPriorite(),
                        baseScore,
                        timeMultiplier,
                        satisfactionPoints,
                        complexityBonus,
                        totalScore
                ))
                .technicien(technicien)
                .ticket(ticket)
                .build();

        pointTransactionRepository.save(transaction);

        Integer currentPoints = pointTransactionRepository.sumByTechnicienId(technicien.getId());
        Double averageNote = pointTransactionRepository.averageByTechnicienId(technicien.getId());

        technicien.setTotalPoints(currentPoints != null ? currentPoints : 0);
        technicien.setNoteMoyenne(averageNote != null ? averageNote : 0.0);

        // Update badges
        updateBadgeAssignment(technicien);

        technicienRepository.save(technicien);
    }

    private void updateBadgeAssignment(Technicien technicien) {
        String badgeName = badgeService.getBadgeByPoints(technicien.getTotalPoints());
        Badge badge = badgeRepository.findByNomBadge(badgeName);
        if (badge != null) {
            // Pour l'instant on garde un seul badge (le plus haut)
            technicien.getBadges().clear();
            technicien.getBadges().add(badge);
        }
    }

    public void updateTechnicianScore(Long technicienId) {
        Technicien technicien = technicienRepository.findById(technicienId)
                .orElseThrow(() -> new RuntimeException("Technicien not found"));

        List<PointTransaction> transactions = pointTransactionRepository.findByTechnicienId(technicienId);
        int totalPoints = transactions.stream().mapToInt(PointTransaction::getQuantite).sum();
        double averageNote = transactions.stream().mapToInt(PointTransaction::getQuantite).average().orElse(0.0);

        technicien.setTotalPoints(totalPoints);
        technicien.setNoteMoyenne(averageNote);
        
        updateBadgeAssignment(technicien);
        
        technicienRepository.save(technicien);
    }
}
