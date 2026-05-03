package tn.enicarthage.plate_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TechnicienDesactivationDTO {
    private String reason;
}
