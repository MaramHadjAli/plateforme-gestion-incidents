package tn.enicarthage.plate_be.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.dtos.MaintenanceDTO;
import tn.enicarthage.plate_be.dtos.MaintenanceRequestDTO;
import tn.enicarthage.plate_be.entities.*;
import tn.enicarthage.plate_be.repositories.EquipementRepository;
import tn.enicarthage.plate_be.repositories.MaintenancePreventiveRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenancePreventiveService {

    private final MaintenancePreventiveRepository maintenanceRepository;
    private final EquipementRepository equipementRepository;


    public List<MaintenanceDTO> getAll() {
        return maintenanceRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MaintenanceDTO getById(Long id) {
        return toDTO(findOrThrow(id));
    }

    public List<MaintenanceDTO> getByEquipement(String idEquipement) {
        return maintenanceRepository.findByEquipement_IdEquipement(idEquipement)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<MaintenanceDTO> getByDateRange(Date start, Date end) {
        return maintenanceRepository.findByDateRange(start, end)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<MaintenanceDTO> getOverdue() {
        return maintenanceRepository.findOverdue(new Date())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MaintenanceDTO create(MaintenanceRequestDTO dto) {

        Equipement equipement = equipementRepository.findById(dto.getIdEquipement())
                .orElseThrow(() -> new RuntimeException("Equipement non trouvé: " + dto.getIdEquipement()));

        MaintenancePreventive m = new MaintenancePreventive();

        m.setDateProgramme(dto.getDateProgramme());
        m.setFrequence(dto.getFrequence() != null ? FREQUENCE_MAINTENANCE.valueOf(dto.getFrequence()) : null);
        m.setDescription(dto.getDescription());
        m.setStatus(STATUS_MAINTENANCE.PLANIFIEE);
        m.setEquipement(equipement);

        return toDTO(maintenanceRepository.save(m));
    }

    public MaintenanceDTO update(Long id, MaintenanceRequestDTO dto) {

        MaintenancePreventive m = findOrThrow(id);

        Equipement equipement = equipementRepository.findById(dto.getIdEquipement())
                .orElseThrow(() -> new RuntimeException("Equipement non trouvé: " + dto.getIdEquipement()));

        m.setDateProgramme(dto.getDateProgramme());
        m.setFrequence(dto.getFrequence() != null ? FREQUENCE_MAINTENANCE.valueOf(dto.getFrequence()) : null);
        m.setDescription(dto.getDescription());
        m.setEquipement(equipement);

        return toDTO(maintenanceRepository.save(m));
    }

    public MaintenanceDTO updateStatus(Long id, STATUS_MAINTENANCE status) {

        MaintenancePreventive m = findOrThrow(id);

        m.setStatus(status);

        if (STATUS_MAINTENANCE.TERMINEE.equals(status)) {
            scheduleNext(m);
        }

        return toDTO(maintenanceRepository.save(m));
    }

    public void delete(Long id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new RuntimeException("Maintenance non trouvée: " + id);
        }
        maintenanceRepository.deleteById(id);
    }

    private MaintenancePreventive findOrThrow(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance non trouvée: " + id));
    }

    private void scheduleNext(MaintenancePreventive completed) {

        if (completed.getFrequence() == null) return;

        Calendar cal = Calendar.getInstance();
        cal.setTime(completed.getDateProgramme());

        switch (completed.getFrequence().name().toUpperCase()) {
            case "HEBDOMADAIRE" -> cal.add(Calendar.WEEK_OF_YEAR, 1);
            case "MENSUELLE"    -> cal.add(Calendar.MONTH, 1);
            case "ANNUELLE"     -> cal.add(Calendar.YEAR, 1);
            default -> { return; }
        }

        MaintenancePreventive next = new MaintenancePreventive();
        next.setDateProgramme(cal.getTime());
        next.setFrequence(completed.getFrequence());
        next.setDescription(completed.getDescription());
        next.setStatus(STATUS_MAINTENANCE.PLANIFIEE);
        next.setEquipement(completed.getEquipement());

        maintenanceRepository.save(next);
    }

    private MaintenanceDTO toDTO(MaintenancePreventive m) {

        MaintenanceDTO dto = new MaintenanceDTO();

        dto.setIdMaintenance(m.getIdMaintenance());
        dto.setDateProgramme(m.getDateProgramme());
        dto.setFrequence(m.getFrequence() != null ? m.getFrequence().name() : null);
        dto.setDescription(m.getDescription());
        dto.setStatus(m.getStatus());

        dto.setEnRetard(
                STATUS_MAINTENANCE.PLANIFIEE.equals(m.getStatus()) &&
                        m.getDateProgramme() != null &&
                        m.getDateProgramme().before(new Date())
        );

        if (m.getEquipement() != null) {
            dto.setIdEquipement(m.getEquipement().getIdEquipement());
            dto.setNomEquipement(m.getEquipement().getNomEquipement());
        }

        return dto;
    }
}