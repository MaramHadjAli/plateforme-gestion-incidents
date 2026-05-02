package tn.enicarthage.plate_be.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.converters.TicketConverter;
import tn.enicarthage.plate_be.dtos.TicketRequestDTO;
import tn.enicarthage.plate_be.dtos.TicketResponseDTO;
import tn.enicarthage.plate_be.entities.Demandeur;
import tn.enicarthage.plate_be.entities.Equipement;
import tn.enicarthage.plate_be.entities.STATUS_TICKET;
import tn.enicarthage.plate_be.entities.Ticket;
import tn.enicarthage.plate_be.repositories.DemandeurRepository;
import tn.enicarthage.plate_be.repositories.TicketRepository;
import tn.enicarthage.plate_be.services.TicketService;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final DemandeurRepository demandeurRepository;
    private final TicketConverter ticketConverter;
    private final AuthenticationService authenticationService;

    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        return authentication.getName();
    }
    @Override
    public TicketResponseDTO createTicket(TicketRequestDTO dto) {
        Demandeur demandeur = demandeurRepository.findByEmail(getCurrentUserId());

        Ticket ticket = ticketConverter.dtoToEntity(dto);

        ticket.setDateCreation(new Date());
        ticket.setStatus(STATUS_TICKET.OUVERT);
        ticket.setCreatedBy(demandeur);

        Ticket savedTicket = ticketRepository.save(ticket);
        return ticketConverter.entityToDto(savedTicket);
    }

    @Override
    public List<TicketResponseDTO> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(ticketConverter::entityToDto)
                .collect(Collectors.toList());
    }
}