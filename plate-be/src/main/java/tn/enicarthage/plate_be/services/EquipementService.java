package tn.enicarthage.plate_be.services;

import tn.enicarthage.plate_be.dtos.EquipementDTO;
import tn.enicarthage.plate_be.dtos.EquipementRequestDTO;
import tn.enicarthage.plate_be.entities.ETAT_EQUIPEMENT;

import java.util.List;

public interface EquipementService {

    List<EquipementDTO> getAll();

    EquipementDTO getById(String id);

    List<EquipementDTO> getBySalle(String idSalle);

    EquipementDTO create(EquipementRequestDTO dto);

    EquipementDTO update(String id, EquipementRequestDTO dto);

    EquipementDTO updateEtat(String id, ETAT_EQUIPEMENT etat);

    void delete(String id);

    List<EquipementDTO> getGarantiesExpirees();
}
