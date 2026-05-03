package tn.enicarthage.plate_be.dtos;

import lombok.Data;

@Data
public class SalleDTO {
    private String idSalle;
    private String nomSalle;
    private String etage;
    private String batiment;
    private int nombreEquipements;
}
