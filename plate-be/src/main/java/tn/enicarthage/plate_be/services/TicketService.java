package tn.enicarthage.plate_be.services;

import tn.enicarthage.plate_be.dtos.TicketRequestDTO;
import tn.enicarthage.plate_be.dtos.TicketResponseDTO;
import tn.enicarthage.plate_be.entities.Ticket;

import java.util.List;

public interface TicketService {
    TicketResponseDTO createTicket(TicketRequestDTO ticketRequestDTO);
    List<TicketResponseDTO> getAllTickets();
    List<TicketResponseDTO> getTicketsByIds(List<String> ids);
    TicketResponseDTO getTicketById(String id);
}