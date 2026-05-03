package tn.enicarthage.plate_be.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import tn.enicarthage.plate_be.dtos.*;
import tn.enicarthage.plate_be.annotations.Loggable;
import tn.enicarthage.plate_be.converters.TechnicienConverter;
import tn.enicarthage.plate_be.entities.PointTransaction;
import tn.enicarthage.plate_be.entities.Technicien;
import tn.enicarthage.plate_be.entities.Utilisateur;
import tn.enicarthage.plate_be.entities.ROLE;
import tn.enicarthage.plate_be.repositories.PointTransactionRepository;
import tn.enicarthage.plate_be.repositories.TechnicienRepository;
import tn.enicarthage.plate_be.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enicarthage.plate_be.utils.PasswordGenerator;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TechnicienServiceImpl implements TechnicienService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TechnicienRepository technicienRepository;

    @Autowired
    private UserRepository utilisateurRepository;

    @Autowired
    private TechnicienConverter technicienConverter;

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PointTransactionRepository pointTransactionRepository;

    @Override
    @Loggable(action = "GET_ALL_TECHNICIENS", description = "Fetch all active technicians")
    public List<TechnicienResponseDTO> getAllTechniciens() {
        List<Utilisateur> techniciens = utilisateurRepository.findByRole(ROLE.TECHNICIEN);
        return technicienConverter.entitiesToDtos(techniciens);
    }

    @Override
    @Loggable(action = "GET_TECHNICIEN_BY_ID", description = "Fetch technician by ID")
    public TechnicienResponseDTO getTechnicienById(Long id) throws Exception {
        Utilisateur technicien = utilisateurRepository.findByIdAndRole(id, ROLE.TECHNICIEN)
                .orElseThrow(() -> new Exception("Technicien not found with id: " + id));
        return technicienConverter.entityToDto(technicien);
    }

    @Override
    public void desactivateTechnicien(Long id, String confidentialReason) {

    }

    @Override
    @Loggable(action = "CREATE_TECHNICIEN", description = "Create new technician")
    public TechnicienResponseDTO createTechnicien(NewTechnicienDTO dto) {
        if (utilisateurRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        String generatedPassword = passwordGenerator.generateRandomPassword();

        Utilisateur technicien = new Utilisateur();
        technicien.setNom(dto.getNom());
        technicien.setPrenom(dto.getPrenom());
        technicien.setEmail(dto.getEmail());
        technicien.setMotPassee(passwordEncoder.encode(generatedPassword));
        technicien.setTelephone(dto.getTelephone());
        technicien.setRole(ROLE.TECHNICIEN);
        technicien.setActive(true);
        technicien.setEnabled(true);
        technicien.setDateInscription(new Date());

        Utilisateur saved = utilisateurRepository.save(technicien);

        emailService.sendTemporaryPasswordEmail(
                dto.getEmail(),
                dto.getNom(),
                dto.getPrenom(),
                generatedPassword
        );

        return technicienConverter.entityToDto(saved);
    }

    @Transactional
    @Loggable(action = "DESACTIVATE_TECHNICIEN", description = "Soft delete technician with confidential reason")
    @Override
    public void desactivateTechnicien(Long id, TechnicienDesactivationDTO dto) throws Exception {
        Utilisateur technicien = utilisateurRepository.findByIdAndRole(id, ROLE.TECHNICIEN)
                .orElseThrow(() -> new Exception("Technicien not found with id: " + id));

        if (!technicien.isActive()) {
            throw new IllegalStateException("Technicien is already desactivated");
        }

        technicien.setActive(false);
        technicien.setDesactivationDate(java.util.Date.from(LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
        technicien.setDesactivationReason(dto.getDesactivationReason());

        utilisateurRepository.save(technicien);
    }

    @Override
    @Loggable(action = "GET_CONFIDENTIAL_REASON", description = "Get confidential desactivation reason")
    public String getConfidentialReason(Long id) throws Exception {
        Utilisateur technicien = utilisateurRepository.findByIdAndRole(id, ROLE.TECHNICIEN)
                .orElseThrow(() -> new Exception("Technicien not found with id: " + id));

        if (technicien.isActive()) {
            throw new IllegalStateException("Technicien is still active, no desactivation reason available");
        }

        return technicien.getDesactivationReason();
    }

    @Override
    @Transactional
    @Loggable(action = "REACTIVATE_TECHNICIEN", description = "Reactivate previously desactivated technician")
    public void reactivateTechnicien(Long id) throws Exception {
        Utilisateur technicien = utilisateurRepository.findByIdAndRole(id, ROLE.TECHNICIEN)
                .orElseThrow(() -> new Exception("Technicien not found with id: " + id));

        if (technicien.isActive()) {
            throw new IllegalStateException("Technicien is already active");
        }

        technicien.setActive(true);
        technicien.setDesactivationDate(null);

        utilisateurRepository.save(technicien);
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
        return utilisateurRepository.findByRole(role);
    }

    @Override
    public TechnicianScoreDTO getTechnicianScoreWithDetails(Long id) {
        Technicien technicien = technicienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Technicien not found"));

        List<TechnicianRankDTO> ranking = getRanking();
        int rank = 0;
        for (int i = 0; i < ranking.size(); i++) {
            if (ranking.get(i).getId().equals(id)) {
                rank = i + 1;
                break;
            }
        }

        List<PointTransaction> transactions = pointTransactionRepository.findByTechnicienId(id);
        List<PointTransactionDTO> transactionDTOs = transactions.stream()
                .map(t -> PointTransactionDTO.builder()
                        .idPoint(t.getIdPoint())
                        .quantite(t.getQuantite())
                        .raison(t.getRaison())
                        .dateAttribution(t.getDateAttribution())
                        .ticketId(t.getTicket() != null ? t.getTicket().getIdTicket() : null)
                        .build())
                .collect(Collectors.toList());

        int totalPoints = technicien.getTotalPoints();
        String currentBadge = badgeService.getBadgeByPoints(totalPoints);
        String nextBadge = badgeService.getNextBadgeName(totalPoints);
        int pointsToNextBadge = badgeService.getPointsForNextBadge(totalPoints);

        return TechnicianScoreDTO.builder()
                .id(technicien.getId())
                .nom(technicien.getNom())
                .prenom(technicien.getPrenom())
                .email(technicien.getEmail())
                .specialite(technicien.getSpecialite())
                .totalPoints(totalPoints)
                .averageNote(technicien.getNoteMoyenne())
                .rank(rank)
                .currentBadge(currentBadge)
                .nextBadge(nextBadge)
                .pointsToNextBadge(pointsToNextBadge)
                .transactions(transactionDTOs)
                .build();
    }

    @Override
    public List<TechnicianRankDTO> getRanking() {
        List<Technicien> techniciens = technicienRepository.findAll();

        List<TechnicianRankDTO> ranking = techniciens.stream()
                .filter(Technicien::isActive)
                .map(tech -> new TechnicianRankDTO(
                        tech.getId(),
                        tech.getNom(),
                        tech.getPrenom(),
                        tech.getEmail(),
                        tech.getTotalPoints(),
                        tech.getNoteMoyenne() != null ? tech.getNoteMoyenne() : 0.0,
                        0,
                        badgeService.getBadgeByPoints(tech.getTotalPoints())
                ))
                .sorted((a, b) -> b.getTotalPoints().compareTo(a.getTotalPoints()))
                .collect(Collectors.toList());

        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).setRank(i + 1);
        }

        return ranking;
    }

    @Override
    public List<PointTransactionDTO> getTechnicianTransactions(Long id) {
        List<PointTransaction> transactions = pointTransactionRepository.findByTechnicienId(id);
        return transactions.stream()
                .map(t -> PointTransactionDTO.builder()
                        .idPoint(t.getIdPoint())
                        .quantite(t.getQuantite())
                        .raison(t.getRaison())
                        .dateAttribution(t.getDateAttribution())
                        .ticketId(t.getTicket() != null ? t.getTicket().getIdTicket() : null)
                        .build())
                .sorted((a, b) -> b.getDateAttribution().compareTo(a.getDateAttribution()))
                .collect(Collectors.toList());
    }
}