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
        ticket.setPriorite(dto.getPriorite());
        ticket.setDateLimiteReparation(dto.getDateLimite());
        return ticket;
    }

    public TicketResponseDTO entityToDto(Ticket ticket) {
        if (ticket == null) return null;

        TicketResponseDTO response = new TicketResponseDTO();
        response.setIdTicket(ticket.getIdTicket());
        response.setTitre(ticket.getTitre());
        response.setDescription(ticket.getDescription());
        response.setDateCreation(ticket.getDateCreation());
        response.setPriorite(ticket.getPriorite());
        response.setStatus(ticket.getStatus());
        response.setDateLimite(ticket.getDateLimiteReparation());

        if (ticket.getCreatedBy()!= null) {
            response.setDemandeurNom(ticket.getCreatedBy().getNom());
        }


        return response;
    }
}