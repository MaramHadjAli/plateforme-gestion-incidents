package tn.enicarthage.plate_be.converters;

import tn.enicarthage.plate_be.dtos.TechnicienResponseDTO;
import tn.enicarthage.plate_be.entities.Utilisateur;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TechnicienConverter {

    public TechnicienResponseDTO entityToDto(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return null;
        }

        TechnicienResponseDTO dto = new TechnicienResponseDTO();
        dto.setIdUser(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setEmail(utilisateur.getEmail());
        dto.setTelephone(utilisateur.getTelephone());
        dto.setDateInscription(utilisateur.getDateInscription());
        dto.setActive(utilisateur.isActive());

        return dto;
    }

    public List<TechnicienResponseDTO> entitiesToDtos(List<Utilisateur> utilisateurs) {
        if (utilisateurs == null) {
            return null;
        }

        return utilisateurs.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }
}