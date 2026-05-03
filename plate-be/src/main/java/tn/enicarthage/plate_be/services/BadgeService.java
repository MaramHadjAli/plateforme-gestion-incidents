package tn.enicarthage.plate_be.services;

import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.entities.Badge;
import tn.enicarthage.plate_be.repositories.BadgeRepository;

import java.util.Arrays;
import java.util.List;

@Service
public class BadgeService {

    private final BadgeRepository badgeRepository;

    public BadgeService(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    public void initializeBadges() {
        if (badgeRepository.count() > 0) {
            return;
        }

        List<Badge> badges = Arrays.asList(
                Badge.builder()
                        .nomBadge("STAGIAIRE")
                        .description("Technicien en formation - Débutant dans la maintenance")
                        .icon("https://cdn-icons-png.flaticon.com/512/1995/1995572.png")
                        .critereObtention("0 - 99 points")
                        .build(),
                Badge.builder()
                        .nomBadge("TECHNICIEN JUNIOR")
                        .description("Technicien confirmé - Bonne maîtrise des bases")
                        .icon("https://cdn-icons-png.flaticon.com/512/3135/3135715.png")
                        .critereObtention("100 - 299 points")
                        .build(),
                Badge.builder()
                        .nomBadge("TECHNICIEN SENIOR")
                        .description("Technicien expérimenté - Résolution rapide et efficace")
                        .icon("https://cdn-icons-png.flaticon.com/512/3135/3135768.png")
                        .critereObtention("300 - 599 points")
                        .build(),
                Badge.builder()
                        .nomBadge("TECHNICIEN EXPERT")
                        .description("Expert en maintenance - Résout les problèmes complexes")
                        .icon("https://cdn-icons-png.flaticon.com/512/3135/3135786.png")
                        .critereObtention("600 - 999 points")
                        .build(),
                Badge.builder()
                        .nomBadge("SUPER TECHNICIEN")
                        .description("Légende de la maintenance - Excellence absolue")
                        .icon("https://cdn-icons-png.flaticon.com/512/1995/1995607.png")
                        .critereObtention("1000+ points")
                        .build()
        );

        badgeRepository.saveAll(badges);
    }

    public String getBadgeByPoints(int points) {
        if (points >= 1000) return "SUPER TECHNICIEN";
        if (points >= 600) return "TECHNICIEN EXPERT";
        if (points >= 300) return "TECHNICIEN SENIOR";
        if (points >= 100) return "TECHNICIEN JUNIOR";
        return "STAGIAIRE";
    }

    public int getPointsForNextBadge(int points) {
        if (points < 100) return 100 - points;
        if (points < 300) return 300 - points;
        if (points < 600) return 600 - points;
        if (points < 1000) return 1000 - points;
        return 0;
    }

    public String getNextBadgeName(int points) {
        if (points < 100) return "TECHNICIEN JUNIOR";
        if (points < 300) return "TECHNICIEN SENIOR";
        if (points < 600) return "TECHNICIEN EXPERT";
        if (points < 1000) return "SUPER TECHNICIEN";
        return "🏆 MAÎTRE ABSOLU";
    }
}
