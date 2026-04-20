package tn.enicarthage.plate_be.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tn.enicarthage.plate_be.dtos.SalleSummary;
import tn.enicarthage.plate_be.repositories.SalleRepository;

@RestController
@RequestMapping("/api/salles")
public class SalleController {
    private final SalleRepository salleRepository;

    public SalleController(SalleRepository salleRepository) {
        this.salleRepository = salleRepository;
    }

    @GetMapping
    public ResponseEntity<List<SalleSummary>> getSalles() {
        List<SalleSummary> data = salleRepository.findAll().stream()
                .map(salle -> new SalleSummary(
                        salle.getIdSalle(),
                        salle.getNomSalle(),
                        salle.getEtage(),
                        salle.getBatiment()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(data);
    }
}

