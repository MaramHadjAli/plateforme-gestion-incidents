package tn.enicarthage.plate_be.dtos;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriqueMaintenanceDTO {
    private Long idHistorique;
    private Date dateExecution;
    private String type;
    private String description;
    private float coutMaintenance;
    private String nomTechnicienResponsable;
    private String idEquipement;
    private String nomEquipement;
    private String ticketId;
}
