package tn.enicarthage.plate_be.services;

import tn.enicarthage.plate_be.dtos.TicketRequestDTO;
import tn.enicarthage.plate_be.dtos.TicketResponseDTO;
import tn.enicarthage.plate_be.entities.STATUS_TICKET;

import java.util.List;

public interface TicketService {
    TicketResponseDTO createTicket(TicketRequestDTO ticketRequestDTO);
    List<TicketResponseDTO> getAllTickets();
    TicketResponseDTO getTicketById(String id);
    List<TicketResponseDTO> getTicketsByDemandeur(String email);
    List<TicketResponseDTO> getTicketsByTechnicien(String email);
    TicketResponseDTO assignTicket(String ticketId, Long technicienId);
    TicketResponseDTO updateStatus(String ticketId, STATUS_TICKET newStatus);
    void deleteTicket(String id);
}