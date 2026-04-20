package tn.enicarthage.plate_be.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tn.enicarthage.plate_be.dtos.EquipementSummary;
import tn.enicarthage.plate_be.repositories.EquipementRepository;

@RestController
@RequestMapping("/api/equipements")
public class EquipementController {
    private final EquipementRepository equipementRepository;

    public EquipementController(EquipementRepository equipementRepository) {
        this.equipementRepository = equipementRepository;
    }

    @GetMapping
    public ResponseEntity<List<EquipementSummary>> getEquipements(
            @RequestParam(value = "salleId", required = false) String salleId) {
        List<EquipementSummary> data = (salleId == null || salleId.isBlank()
                ? equipementRepository.findAll()
                : equipementRepository.findBySalle_IdSalle(salleId))
                .stream()
                .map(equipement -> new EquipementSummary(
                        equipement.getIdEquipement(),
                        equipement.getNomEquipement(),
                        equipement.getType(),
                        equipement.getModele(),
                        equipement.getNumSerie(),
                        equipement.getEtat() != null ? equipement.getEtat().name() : null,
                        equipement.getSalle() != null ? equipement.getSalle().getIdSalle() : null
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(data);
    }
}

