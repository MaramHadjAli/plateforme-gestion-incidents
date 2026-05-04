package tn.enicarthage.plate_be.dtos;

import lombok.Getter;
import lombok.Setter;
import tn.enicarthage.plate_be.entities.STATUS_MAINTENANCE;
import java.util.Date;

@Getter
@Setter
public class MaintenanceDTO {
    private Long idMaintenance;
    private Date dateProgramme;
    private String frequence;
    private String description;
    private STATUS_MAINTENANCE status;
    private String idEquipement;
    private String nomEquipement;
    private String salleNom;
    private String type;
    private boolean enRetard;
}
