package tn.enicarthage.plate_be.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tn.enicarthage.plate_be.entities.ETAT_EQUIPEMENT;

import java.util.Date;

@Data
public class EquipementRequestDTO {

    @NotBlank
    private String idEquipement;

    @NotBlank
    private String nomEquipement;

    @NotBlank
    private String type;

    private String modele;
    private String numSerie;

    @NotNull
    private ETAT_EQUIPEMENT etat;

    private Date dateAchat;
    private Date dateFinGarantie;

    @NotBlank
    private String idSalle;
}
