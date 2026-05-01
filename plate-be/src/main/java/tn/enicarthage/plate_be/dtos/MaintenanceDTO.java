package tn.enicarthage.plate_be.dtos;

import lombok.Data;
import tn.enicarthage.plate_be.entities.STATUS_MAINTENANCE;

import java.util.Date;

@Data
public class MaintenanceDTO {
    private Long idMaintenance;
    private Date dateProgramme;
    private String frequence;
    private String description;
    private STATUS_MAINTENANCE status;
    private String idEquipement;
    private String nomEquipement;
    private boolean enRetard;
}