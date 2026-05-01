package tn.enicarthage.plate_be.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.dtos.SalleDTO;
import tn.enicarthage.plate_be.dtos.SalleRequestDTO;
import tn.enicarthage.plate_be.entities.Salle;
import tn.enicarthage.plate_be.repositories.SalleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalleServiceImpl implements SalleService {

    private final SalleRepository salleRepository;

    @Override
    public List<SalleDTO> getAll() {
        return salleRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SalleDTO getById(String id) {
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salle non trouvée: " + id));
        return toDTO(salle);
    }

    @Override
    public SalleDTO create(SalleRequestDTO dto) {

        if (salleRepository.existsById(dto.getIdSalle())) {
            throw new RuntimeException("ID déjà existant");
        }

        if (salleRepository.existsByNomSalle(dto.getNomSalle())) {
            throw new RuntimeException("Nom de salle déjà utilisé");
        }

        Salle salle = new Salle();
        salle.setIdSalle(dto.getIdSalle());
        salle.setNomSalle(dto.getNomSalle());
        salle.setEtage(dto.getEtage());
        salle.setBatiment(dto.getBatiment());

        return toDTO(salleRepository.save(salle));
    }

    @Override
    public SalleDTO update(String id, SalleRequestDTO dto) {

        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salle non trouvée"));

        salle.setNomSalle(dto.getNomSalle());
        salle.setEtage(dto.getEtage());
        salle.setBatiment(dto.getBatiment());

        return toDTO(salleRepository.save(salle));
    }

    @Override
    public void delete(String id) {
        if (!salleRepository.existsById(id)) {
            throw new RuntimeException("Salle non trouvée");
        }
        salleRepository.deleteById(id);
    }

    private SalleDTO toDTO(Salle salle) {
        SalleDTO dto = new SalleDTO();
        dto.setIdSalle(salle.getIdSalle());
        dto.setNomSalle(salle.getNomSalle());
        dto.setEtage(salle.getEtage());
        dto.setBatiment(salle.getBatiment());

        dto.setNombreEquipements(
                salle.getEquipements() != null ? salle.getEquipements().size() : 0
        );

        return dto;
    }
}