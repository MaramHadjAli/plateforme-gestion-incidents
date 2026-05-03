package tn.enicarthage.plate_be.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.dtos.EquipementDTO;
import tn.enicarthage.plate_be.dtos.EquipementRequestDTO;
import tn.enicarthage.plate_be.entities.ETAT_EQUIPEMENT;
import tn.enicarthage.plate_be.entities.Equipement;
import tn.enicarthage.plate_be.entities.Salle;
import tn.enicarthage.plate_be.repositories.EquipementRepository;
import tn.enicarthage.plate_be.repositories.SalleRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipementServiceImpl implements EquipementService {

    private final EquipementRepository equipementRepository;
    private final SalleRepository salleRepository;

    @Override
    public List<EquipementDTO> getAll() {
        return equipementRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EquipementDTO getById(String id) {
        return toDTO(findOrThrow(id));
    }

    @Override
    public List<EquipementDTO> getBySalle(String idSalle) {
        return equipementRepository.findBySalle_IdSalle(idSalle)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EquipementDTO create(EquipementRequestDTO dto) {

        if (equipementRepository.existsById(dto.getIdEquipement())) {
            throw new RuntimeException("Equipement déjà existant");
        }

        Salle salle = salleRepository.findById(dto.getIdSalle())
                .orElseThrow(() -> new RuntimeException("Salle non trouvée"));

        Equipement eq = mapToEntity(dto, new Equipement());
        eq.setSalle(salle);

        return toDTO(equipementRepository.save(eq));
    }

    @Override
    public EquipementDTO update(String id, EquipementRequestDTO dto) {

        Equipement eq = findOrThrow(id);

        Salle salle = salleRepository.findById(dto.getIdSalle())
                .orElseThrow(() -> new RuntimeException("Salle non trouvée"));

        mapToEntity(dto, eq);
        eq.setSalle(salle);

        return toDTO(equipementRepository.save(eq));
    }

    @Override
    public EquipementDTO updateEtat(String id, ETAT_EQUIPEMENT etat) {

        Equipement eq = findOrThrow(id);
        eq.setEtat(etat);

        return toDTO(equipementRepository.save(eq));
    }

    @Override
    public void delete(String id) {
        if (!equipementRepository.existsById(id)) {
            throw new RuntimeException("Equipement non trouvé");
        }
        equipementRepository.deleteById(id);
    }

    @Override
    public List<EquipementDTO> getGarantiesExpirees() {
        return equipementRepository.findByDateFinGarantieBefore(new Date())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 🔥 helpers

    private Equipement findOrThrow(String id) {
        return equipementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipement non trouvé"));
    }

    private Equipement mapToEntity(EquipementRequestDTO dto, Equipement eq) {
        eq.setIdEquipement(dto.getIdEquipement());
        eq.setNomEquipement(dto.getNomEquipement());
        eq.setType(dto.getType());
        eq.setModele(dto.getModele());
        eq.setNumSerie(dto.getNumSerie());
        eq.setEtat(dto.getEtat());
        eq.setDateAchat(dto.getDateAchat());
        eq.setDateFinGarantie(dto.getDateFinGarantie());
        return eq;
    }

    private EquipementDTO toDTO(Equipement eq) {
        EquipementDTO dto = new EquipementDTO();

        dto.setIdEquipement(eq.getIdEquipement());
        dto.setNomEquipement(eq.getNomEquipement());
        dto.setType(eq.getType());
        dto.setModele(eq.getModele());
        dto.setNumSerie(eq.getNumSerie());
        dto.setEtat(eq.getEtat());
        dto.setDateAchat(eq.getDateAchat());
        dto.setDateFinGarantie(eq.getDateFinGarantie());

        dto.setGarantieExpiree(
                eq.getDateFinGarantie() != null &&
                        eq.getDateFinGarantie().before(new Date())
        );

        if (eq.getSalle() != null) {
            dto.setIdSalle(eq.getSalle().getIdSalle());
            dto.setNomSalle(eq.getSalle().getNomSalle());
        }

        return dto;
    }
}
