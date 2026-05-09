package tn.enicarthage.plate_be.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class MaintenanceRequestDTO {

    @NotNull(message = "Date programmée est obligatoire")
    private Date dateProgramme;

    @NotBlank(message = "Fréquence est obligatoire")
    private String frequence;

    private String description;

    @NotBlank(message = "ID Equipement est obligatoire")
    private String idEquipement;
}
