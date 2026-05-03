package tn.enicarthage.plate_be.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enicarthage.plate_be.dtos.*;
import tn.enicarthage.plate_be.entities.*;
import tn.enicarthage.plate_be.repositories.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TechnicienServiceImpl implements TechnicienService {

    private final TechnicienRepository technicienRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PointTransactionRepository pointTransactionRepository;
    private final BadgeRepository badgeRepository;

    @Override
    public List<TechnicienResponseDTO> getAllTechniciens() {
        return technicienRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TechnicienResponseDTO getTechnicienById(Long id) throws Exception {
        Technicien tech = technicienRepository.findById(id)
                .orElseThrow(() -> new Exception("Technicien non trouvé"));
        return mapToResponseDTO(tech);
    }

    @Override
    public void desactivateTechnicien(Long id, String confidentialReason) {
        technicienRepository.findById(id).ifPresent(tech -> {
            tech.setActive(false);
            tech.setDesactivationReason(confidentialReason);
            tech.setDesactivationDate(new Date());
            technicienRepository.save(tech);
        });
    }

    @Override
    public TechnicienResponseDTO createTechnicien(NewTechnicienDTO dto) {
        Technicien tech = new Technicien();
        tech.setNom(dto.getNom());
        tech.setPrenom(dto.getPrenom());
        tech.setEmail(dto.getEmail());
        tech.setTelephone(dto.getTelephone());
        tech.setRole(ROLE.valueOf(dto.getRole()));
        tech.setMotPassee(passwordEncoder.encode("Default123!")); // Default password
        tech.setActive(true);
        tech.setEnabled(true);
        tech.setDateInscription(new Date());
        tech.setSpecialite(dto.getSpecialite());
        
        Technicien saved = technicienRepository.save(tech);
        return mapToResponseDTO(saved);
    }

    @Override
    @Transactional
    public void desactivateTechnicien(Long id, TechnicienDesactivationDTO dto) throws Exception {
        desactivateTechnicien(id, dto.getReason());
    }

    @Override
    public String getConfidentialReason(Long id) throws Exception {
        return technicienRepository.findById(id)
                .map(Utilisateur::getDesactivationReason)
                .orElseThrow(() -> new Exception("Technicien non trouvé"));
    }

    @Override
    public void reactivateTechnicien(Long id) throws Exception {
        Technicien tech = technicienRepository.findById(id)
                .orElseThrow(() -> new Exception("Technicien non trouvé"));
        tech.setActive(true);
        tech.setDesactivationReason(null);
        tech.setDesactivationDate(null);
        technicienRepository.save(tech);
    }

    @Override
    public Optional<Technicien> findById(Long id) {
        return technicienRepository.findById(id);
    }

    @Override
    public List<Technicien> findAllTechniciens() {
        return technicienRepository.findAll();
    }

    @Override
    public List<Utilisateur> findByRole(ROLE role) {
        return userRepository.findByRole(role);
    }

    @Override
    public TechnicianScoreDTO getTechnicianScoreWithDetails(Long id) {
        Technicien tech = technicienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Technicien non trouvé"));

        List<Technicien> allTechs = technicienRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Technicien::getTotalPoints).reversed())
                .collect(Collectors.toList());

        int rank = 1;
        for (int i = 0; i < allTechs.size(); i++) {
            if (allTechs.get(i).getId().equals(tech.getId())) {
                rank = i + 1;
                break;
            }
        }

        List<PointTransactionDTO> transactions = pointTransactionRepository.findByTechnicienOrderByDateAttributionDesc(tech)
                .stream()
                .map(this::mapToTransactionDTO)
                .collect(Collectors.toList());

        return TechnicianScoreDTO.builder()
                .id(tech.getId())
                .nom(tech.getNom())
                .prenom(tech.getPrenom())
                .email(tech.getEmail())
                .specialite(tech.getSpecialite())
                .totalPoints(tech.getTotalPoints())
                .averageNote(tech.getNoteMoyenne())
                .rank(rank)
                .currentBadge(tech.getBadges().isEmpty() ? "STAGIAIRE" : tech.getBadges().iterator().next().getNomBadge())
                .transactions(transactions)
                .build();
    }

    @Override
    public List<TechnicienRankingDTO> getRanking() {
        List<Technicien> allTechs = technicienRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Technicien::getTotalPoints).reversed())
                .collect(Collectors.toList());

        List<TechnicienRankingDTO> ranking = new ArrayList<>();
        for (int i = 0; i < allTechs.size(); i++) {
            Technicien t = allTechs.get(i);
            ranking.add(TechnicienRankingDTO.builder()
                    .id(t.getId())
                    .nom(t.getNom())
                    .prenom(t.getPrenom())
                    .email(t.getEmail())
                    .totalPoints(t.getTotalPoints())
                    .averageNote(t.getNoteMoyenne())
                    .rank(i + 1)
                    .badge(t.getBadges().isEmpty() ? "STAGIAIRE" : t.getBadges().iterator().next().getNomBadge())
                    .build());
        }
        return ranking;
    }

    @Override
    public List<PointTransactionDTO> getTechnicianTransactions(Long id) {
        Technicien tech = technicienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Technicien non trouvé"));
        
        return pointTransactionRepository.findByTechnicienOrderByDateAttributionDesc(tech)
                .stream()
                .map(this::mapToTransactionDTO)
                .collect(Collectors.toList());
    }

    private TechnicienResponseDTO mapToResponseDTO(Technicien tech) {
        TechnicienResponseDTO dto = new TechnicienResponseDTO();
        dto.setIdUser(tech.getId());
        dto.setNom(tech.getNom());
        dto.setPrenom(tech.getPrenom());
        dto.setEmail(tech.getEmail());
        dto.setTelephone(tech.getTelephone());
        dto.setDateInscription(tech.getDateInscription());
        dto.setActive(tech.isActive());
        return dto;
    }

    private PointTransactionDTO mapToTransactionDTO(PointTransaction pt) {
        return PointTransactionDTO.builder()
                .idPoint(pt.getIdPoint())
                .quantite(pt.getQuantite())
                .raison(pt.getRaison())
                .dateAttribution(pt.getDateAttribution())
                .ticketId(pt.getTicket() != null ? pt.getTicket().getIdTicket() : null)
                .build();
    }
}
