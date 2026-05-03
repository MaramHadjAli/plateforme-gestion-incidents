package tn.enicarthage.plate_be.services;

import org.springframework.transaction.annotation.Transactional;
import tn.enicarthage.plate_be.annotations.Loggable;
import tn.enicarthage.plate_be.dtos.TechnicienDesactivationDTO;
import tn.enicarthage.plate_be.dtos.TechnicienResponseDTO;
import java.util.List;
import java.util.Optional;

import tn.enicarthage.plate_be.dtos.NewTechnicienDTO;
import tn.enicarthage.plate_be.entities.ROLE;
import tn.enicarthage.plate_be.entities.Technicien;
import tn.enicarthage.plate_be.entities.Utilisateur;
import tn.enicarthage.plate_be.dtos.TechnicianScoreDTO;
import tn.enicarthage.plate_be.dtos.TechnicianRankDTO;
import tn.enicarthage.plate_be.dtos.PointTransactionDTO;

public interface TechnicienService {

    List<TechnicienResponseDTO> getAllTechniciens();

    TechnicienResponseDTO getTechnicienById(Long id) throws Exception;

    void desactivateTechnicien(Long id, String confidentialReason);

    TechnicienResponseDTO createTechnicien(NewTechnicienDTO dto);

    @Transactional
    @Loggable(action = "DESACTIVATE_TECHNICIEN", description = "Soft delete technician with confidential reason")
    void desactivateTechnicien(Long id, TechnicienDesactivationDTO dto) throws Exception;

    String getConfidentialReason(Long id) throws Exception;

    void reactivateTechnicien(Long id) throws Exception;

    Optional<Technicien> findById(Long id);

    List<Technicien> findAllTechniciens();

    List<Utilisateur> findByRole(ROLE role);

    TechnicianScoreDTO getTechnicianScoreWithDetails(Long id);

    List<TechnicianRankDTO> getRanking();

    List<PointTransactionDTO> getTechnicianTransactions(Long id);
}