package tn.enicarthage.plate_be.converters;

import org.springframework.stereotype.Component;
import tn.enicarthage.plate_be.dtos.TicketRequestDTO;
import tn.enicarthage.plate_be.dtos.TicketResponseDTO;
import tn.enicarthage.plate_be.entities.Ticket;

@Component
public class TicketConverter {

    public Ticket dtoToEntity(TicketRequestDTO dto) {
        if (dto == null) return null;

        Ticket ticket = new Ticket();
        ticket.setTitre(dto.getTitre());
        ticket.setDescription(dto.getDescription());
        ticket.setDateLimiteReparation(dto.getDateLimite());

        if (dto.getPriorite() != null) {
            String p = dto.getPriorite().trim().toUpperCase();
            if (p.equals("NORMAL")) p = "NORMALE";
            try {
                ticket.setPriorite(tn.enicarthage.plate_be.entities.PRIORITE_TICKET.valueOf(p));
            } catch (IllegalArgumentException e) {
                ticket.setPriorite(tn.enicarthage.plate_be.entities.PRIORITE_TICKET.NORMALE);
            }
        }
        
        return ticket;
    }

    public TicketResponseDTO entityToDto(Ticket ticket) {
        if (ticket == null) return null;

        TicketResponseDTO response = new TicketResponseDTO();
        response.setIdTicket(ticket.getIdTicket());
        response.setTitre(ticket.getTitre());
        response.setDescription(ticket.getDescription());
        response.setDateCreation(ticket.getDateCreation());
        response.setDateCloture(ticket.getDateCloture());
        response.setPriorite(ticket.getPriorite());
        response.setStatus(ticket.getStatus());
        response.setDateLimite(ticket.getDateLimiteReparation());

        if (ticket.getCreatedBy() != null) {
            response.setDemandeurNom(ticket.getCreatedBy().getNom());
            response.setDemandeurEmail(ticket.getCreatedBy().getEmail());
        }

        if (ticket.getAssignedTo() != null) {
            response.setTechnicienNom(ticket.getAssignedTo().getNom());
            response.setTechnicienId(ticket.getAssignedTo().getId());
        }

        if (ticket.getSalle() != null) {
            response.setIdSalle(ticket.getSalle().getIdSalle());
            response.setNomSalle(ticket.getSalle().getNomSalle());
        }

        if (ticket.getEquipement() != null) {
            response.setIdEquipement(ticket.getEquipement().getIdEquipement());
            response.setNomEquipement(ticket.getEquipement().getNomEquipement());
        }

        return response;
    }
}