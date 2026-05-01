package tn.enicarthage.plate_be.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.converters.TicketConverter;
import tn.enicarthage.plate_be.dtos.TicketRequestDTO;
import tn.enicarthage.plate_be.dtos.TicketResponseDTO;
import tn.enicarthage.plate_be.entities.*;
import tn.enicarthage.plate_be.repositories.DemandeurRepository;
import tn.enicarthage.plate_be.repositories.TechnicienRepository;
import tn.enicarthage.plate_be.repositories.TicketRepository;
import tn.enicarthage.plate_be.repositories.EquipementRepository;
import tn.enicarthage.plate_be.repositories.SalleRepository;
import tn.enicarthage.plate_be.websocket.NotificationService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final DemandeurRepository demandeurRepository;
    private final TechnicienRepository technicienRepository;
    private final TicketConverter ticketConverter;
    private final NotificationService notificationService;
    private final SalleRepository salleRepository;
    private final EquipementRepository equipementRepository;

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        return authentication.getName();
    }

    @Override
    public TicketResponseDTO createTicket(TicketRequestDTO dto) {
        Demandeur demandeur = demandeurRepository.findByEmail(getCurrentUserEmail());

        Ticket ticket = ticketConverter.dtoToEntity(dto);
        ticket.setDateCreation(new Date());
        ticket.setStatus(STATUS_TICKET.OUVERT);

        if (demandeur != null) {
            ticket.setCreatedBy(demandeur);
        }

        if (dto.getIdSalle() != null && !dto.getIdSalle().isEmpty()) {
            salleRepository.findById(dto.getIdSalle()).ifPresent(ticket::setSalle);
        }

        if (dto.getIdEquipement() != null && !dto.getIdEquipement().isEmpty()) {
            equipementRepository.findById(dto.getIdEquipement()).ifPresent(ticket::setEquipement);
        }

        Ticket savedTicket = ticketRepository.save(ticket);
        
        // Notify Admins (non-blocking)
        try {
            tn.enicarthage.plate_be.dtos.NotificationMessage msg = new tn.enicarthage.plate_be.dtos.NotificationMessage(
                "NEW_TICKET", "Nouveau Ticket", "Un nouveau ticket a été créé: " + savedTicket.getTitre(),
                savedTicket.getIdTicket(), "SYSTEM", "INFO"
            );
            notificationService.notifyAdmin(msg);
        } catch (Exception e) {
            // Don't let notification failure prevent ticket creation
            System.err.println("Notification failed (non-blocking): " + e.getMessage());
        }

        return ticketConverter.entityToDto(savedTicket);
    }

    @Override
    public List<TicketResponseDTO> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(ticketConverter::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TicketResponseDTO getTicketById(String id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé: " + id));
        return ticketConverter.entityToDto(ticket);
    }

    @Override
    public List<TicketResponseDTO> getTicketsByDemandeur(String email) {
        Demandeur demandeur = demandeurRepository.findByEmail(email);
        if (demandeur == null) {
            return List.of();
        }
        return ticketRepository.findByCreatedBy_Id(demandeur.getId())
                .stream()
                .map(ticketConverter::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketResponseDTO> getTicketsByTechnicien(String email) {
        Technicien tech = technicienRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Technicien non trouvé: " + email));
        return ticketRepository.findByAssignedTo_Id(tech.getId())
                .stream()
                .map(ticketConverter::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TicketResponseDTO assignTicket(String ticketId, Long technicienId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé: " + ticketId));
        Technicien tech = technicienRepository.findById(technicienId)
                .orElseThrow(() -> new RuntimeException("Technicien non trouvé: " + technicienId));

        ticket.setAssignedTo(tech);
        ticket.setStatus(STATUS_TICKET.ASSIGNE);
        Ticket saved = ticketRepository.save(ticket);

        // Notify Technician
        notificationService.notifyTicketAssigned(tech, saved.getIdTicket(), saved.getTitre());
        
        // Notify Demandeur
        if (saved.getCreatedBy() != null) {
            notificationService.notifyStatusChanged(saved.getCreatedBy(), saved.getIdTicket(), "ASSIGNE");
        }

        return ticketConverter.entityToDto(saved);
    }

    @Override
    public TicketResponseDTO updateStatus(String ticketId, STATUS_TICKET newStatus) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé: " + ticketId));

        ticket.setStatus(newStatus);

        if (newStatus == STATUS_TICKET.RESOLU || newStatus == STATUS_TICKET.FERME) {
            ticket.setDateCloture(new Date());
        }

        Ticket saved = ticketRepository.save(ticket);

        // Notify Demandeur
        if (saved.getCreatedBy() != null) {
            notificationService.notifyStatusChanged(saved.getCreatedBy(), saved.getIdTicket(), newStatus.name());
        }

        return ticketConverter.entityToDto(saved);
    }

    @Override
    public void deleteTicket(String id) {
        if (!ticketRepository.existsById(id)) {
            throw new RuntimeException("Ticket non trouvé: " + id);
        }
        ticketRepository.deleteById(id);
    }
}