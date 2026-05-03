package tn.enicarthage.plate_be.dtos;

import lombok.Data;
import tn.enicarthage.plate_be.entities.ETAT_EQUIPEMENT;

import java.util.Date;

@Data
public class EquipementDTO {
    private String idEquipement;
    private String nomEquipement;
    private String type;
    private String modele;
    private String numSerie;
    private ETAT_EQUIPEMENT etat;
    private Date dateAchat;
    private Date dateFinGarantie;
    private String idSalle;
    private String nomSalle;
    private boolean garantieExpiree;
}
