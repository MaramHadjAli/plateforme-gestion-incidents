package tn.enicarthage.plate_be.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SalleRequestDTO {
    @NotBlank(message = "L'ID est obligatoire")
    private String idSalle;

    @NotBlank(message = "Le nom est obligatoire")
    private String nomSalle;

    private String etage;

    @NotBlank(message = "Le bâtiment est obligatoire")
    private String batiment;
}
