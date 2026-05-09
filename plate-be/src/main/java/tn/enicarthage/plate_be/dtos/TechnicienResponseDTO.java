package tn.enicarthage.plate_be.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TechnicienResponseDTO {
    private Long idUser;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String specialite;
    private Date dateInscription;
    private boolean isActive;
}
