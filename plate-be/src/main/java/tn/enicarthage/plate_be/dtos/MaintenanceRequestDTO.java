package tn.enicarthage.plate_be.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class MaintenanceRequestDTO {

    @NotNull
    private Date dateProgramme;

    @NotBlank
    private String frequence;

    private String description;

    @NotBlank
    private String idEquipement;
}