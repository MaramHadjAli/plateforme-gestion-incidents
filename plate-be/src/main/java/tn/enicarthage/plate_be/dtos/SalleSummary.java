package tn.enicarthage.plate_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SalleSummary {
    private String idSalle;
    private String nomSalle;
    private String etage;
    private String batiment;
}

