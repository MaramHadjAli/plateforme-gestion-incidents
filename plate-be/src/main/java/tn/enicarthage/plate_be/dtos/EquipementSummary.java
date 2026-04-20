package tn.enicarthage.plate_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EquipementSummary {
    private String idEquipement;
    private String nomEquipement;
    private String type;
    private String modele;
    private String numSerie;
    private String etat;
    private String idSalle;
}

