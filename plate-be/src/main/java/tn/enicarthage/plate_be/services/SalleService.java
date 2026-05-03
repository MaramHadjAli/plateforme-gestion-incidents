package tn.enicarthage.plate_be.services;

import tn.enicarthage.plate_be.dtos.SalleDTO;
import tn.enicarthage.plate_be.dtos.SalleRequestDTO;

import java.util.List;

public interface SalleService {

    List<SalleDTO> getAll();

    SalleDTO getById(String id);

    SalleDTO create(SalleRequestDTO dto);

    SalleDTO update(String id, SalleRequestDTO dto);

    void delete(String id);
}
