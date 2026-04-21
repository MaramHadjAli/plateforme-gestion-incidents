package tn.enicarthage.plate_be.services;

import org.springframework.web.multipart.MultipartFile;
import tn.enicarthage.plate_be.dtos.TicketCreateRequest;
import tn.enicarthage.plate_be.dtos.TicketCreateResponse;
import tn.enicarthage.plate_be.dtos.TicketRequestDTO;
import tn.enicarthage.plate_be.dtos.TicketResponseDTO;
import tn.enicarthage.plate_be.entities.Ticket;

import java.util.List;

public interface TicketService {

    TicketResponseDTO createTicket(TicketRequestDTO ticketRequestDTO);
    List<TicketResponseDTO> getAllTickets();
    TicketResponseDTO getTicketById(String id);
    List<TicketResponseDTO> getTicketsByIds(List<String> ids);

    TicketCreateResponse createTicket(TicketCreateRequest request, MultipartFile file);

    List<Ticket> getTicketsByCurrentUser();
}