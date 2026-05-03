package tn.enicarthage.plate_be.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TechnicienDesactivationDTO {
    @NotBlank(message = "Reason is required")
    private String desactivationReason;
}
