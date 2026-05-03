package tn.enicarthage.plate_be.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.dtos.HistoriqueMaintenanceDTO;
import tn.enicarthage.plate_be.entities.HistoriqueMaintenance;
import tn.enicarthage.plate_be.repositories.HistoriqueMaintenanceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoriqueMaintenanceService {

    private final HistoriqueMaintenanceRepository historiqueRepository;

    public List<HistoriqueMaintenanceDTO> getAll() {
        return historiqueRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<HistoriqueMaintenanceDTO> getByEquipement(String idEquipement) {
        return historiqueRepository.findByEquipementIdEquipementOrderByDateExecutionDesc(idEquipement)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<HistoriqueMaintenanceDTO> getByTechnicien(Long technicienId) {
        return historiqueRepository.findByTechnicienIdOrderByDateExecutionDesc(technicienId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public HistoriqueMaintenance save(HistoriqueMaintenance historique) {
        return historiqueRepository.save(historique);
    }

    private HistoriqueMaintenanceDTO toDTO(HistoriqueMaintenance h) {
        return HistoriqueMaintenanceDTO.builder()
                .idHistorique(h.getIdHistorique())
                .dateExecution(h.getDateExecution())
                .type(h.getType())
                .description(h.getDescription())
                .coutMaintenance(h.getCoutMaintenance())
                .nomTechnicienResponsable(h.getNomTechnicienResponsable())
                .idEquipement(h.getEquipement() != null ? h.getEquipement().getIdEquipement() : null)
                .nomEquipement(h.getEquipement() != null ? h.getEquipement().getNomEquipement() : null)
                .ticketId(h.getTicket() != null ? h.getTicket().getIdTicket() : null)
                .build();
    }
}
