package tn.enicarthage.plate_be.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.dtos.PointTransactionDTO;
import tn.enicarthage.plate_be.dtos.TechnicienRankingDTO;
import tn.enicarthage.plate_be.dtos.TechnicianScoreDTO;
import tn.enicarthage.plate_be.entities.Technicien;
import tn.enicarthage.plate_be.repositories.PointTransactionRepository;
import tn.enicarthage.plate_be.repositories.TechnicienRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassementServiceImpl implements ClassementService {

    private final TechnicienRepository technicienRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final BadgeService badgeService;

    @Override
    public List<TechnicienRankingDTO> getGlobalRanking() {
        List<Technicien> techniciens = technicienRepository.findAll();
        
        // Trier par points décroissants
        techniciens.sort(Comparator.comparingInt(Technicien::getTotalPoints).reversed());

        List<TechnicienRankingDTO> ranking = new ArrayList<>();
        for (int i = 0; i < techniciens.size(); i++) {
            Technicien t = techniciens.get(i);
            ranking.add(TechnicienRankingDTO.builder()
                    .id(t.getId())
                    .nom(t.getNom())
                    .prenom(t.getPrenom())
                    .email(t.getEmail())
                    .totalPoints(t.getTotalPoints())
                    .averageNote(t.getNoteMoyenne())
                    .rank(i + 1)
                    .badge(badgeService.getBadgeByPoints(t.getTotalPoints()))
                    .build());
        }
        return ranking;
    }

    @Override
    public TechnicianScoreDTO getTechnicianScoreDetails(String email) {
        List<TechnicienRankingDTO> globalRanking = getGlobalRanking();
        
        TechnicienRankingDTO myRank = globalRanking.stream()
                .filter(r -> r.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Technicien non trouvé"));

        Technicien tech = technicienRepository.findByEmail(email).get();
        
        List<PointTransactionDTO> transactions = pointTransactionRepository.findByTechnicienOrderByDateAttributionDesc(tech)
                .stream()
                .map(tr -> PointTransactionDTO.builder()
                        .idPoint(tr.getIdPoint())
                        .quantite(tr.getQuantite())
                        .raison(tr.getRaison())
                        .dateAttribution(tr.getDateAttribution())
                        .ticketId(tr.getTicket() != null ? tr.getTicket().getIdTicket() : null)
                        .build())
                .collect(Collectors.toList());


        return TechnicianScoreDTO.builder()
                .id(tech.getId())
                .nom(tech.getNom())
                .prenom(tech.getPrenom())
                .email(tech.getEmail())
                .specialite(tech.getSpecialite())
                .totalPoints(tech.getTotalPoints())
                .averageNote(tech.getNoteMoyenne())
                .rank(myRank.getRank())
                .currentBadge(myRank.getBadge())
                .nextBadge(badgeService.getNextBadgeName(tech.getTotalPoints()))
                .pointsToNextBadge(badgeService.getPointsForNextBadge(tech.getTotalPoints()))
                .transactions(transactions)
                .build();
    }


}
