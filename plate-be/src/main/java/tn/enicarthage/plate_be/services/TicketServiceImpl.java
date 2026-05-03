package tn.enicarthage.plate_be.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.converters.TicketConverter;
import tn.enicarthage.plate_be.dtos.*;
import tn.enicarthage.plate_be.entities.*;
import tn.enicarthage.plate_be.repositories.*;
import tn.enicarthage.plate_be.websocket.NotificationService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final DemandeurRepository demandeurRepository;
    private final TechnicienRepository technicienRepository;
    private final UserRepository userRepository;
    private final TicketConverter ticketConverter;
    private final NotificationService notificationService;
    private final SalleRepository salleRepository;
    private final EquipementRepository equipementRepository;
    private final EmailService emailService;
    private final ScoreCalculatorService scoreCalculatorService;

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
        
        ticket.setIdTicket(java.util.UUID.randomUUID().toString());
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

        // Initialize default values
        ticket.setDemandePrixSent(false);
        ticket.setInterestedTechnicians(new ArrayList<>());
        ticket.setDeclinedTechnicians(new ArrayList<>());

        Ticket savedTicket = ticketRepository.save(ticket);

        try {
            NotificationMessage msg = new NotificationMessage(
                    "NEW_TICKET", "Nouveau Ticket", "Un nouveau ticket a été créé: " + savedTicket.getTitre(),
                    savedTicket.getIdTicket(), "SYSTEM", "INFO"
            );
            notificationService.notifyAdmin(msg);
        } catch (Exception e) {
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
        Utilisateur tech = userRepository.findByEmailAndRole(email, ROLE.TECHNICIEN)
                .orElseThrow(() -> new RuntimeException("Technicien non trouvé: " + email));
        return ticketRepository.findByAssignedTo_Id(tech.getId())
                .stream()
                .map(ticketConverter::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TicketResponseDTO assignTicket(String ticketId, Long technicienId) {
        System.out.println("Assigning ticket " + ticketId + " to technician " + technicienId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé: " + ticketId));

        Utilisateur tech = userRepository.findByIdAndRole(technicienId, ROLE.TECHNICIEN)
                .orElseThrow(() -> new RuntimeException("Technicien non trouvé: " + technicienId));

        System.out.println("Found technician: " + tech.getNom() + " " + tech.getPrenom());

        ticket.setAssignedTo((Technicien) tech);
        ticket.setStatus(STATUS_TICKET.ASSIGNE);
        Ticket saved = ticketRepository.save(ticket);

        try {
            notificationService.notifyTicketAssigned(tech, saved.getIdTicket(), saved.getTitre());

            if (saved.getCreatedBy() != null) {
                notificationService.notifyStatusChanged(saved.getCreatedBy(), saved.getIdTicket(), "ASSIGNE");
            }
        } catch (Exception e) {
            System.err.println("Notification failed: " + e.getMessage());
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

    @Override
    public TicketResponseDTO closeTicket(String ticketId) {
        System.out.println("Closing ticket: " + ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé: " + ticketId));

        String currentUserEmail = getCurrentUserEmail();
        Utilisateur currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (currentUser.getRole() == ROLE.TECHNICIEN) {
            if (ticket.getAssignedTo() == null || !ticket.getAssignedTo().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Vous n'êtes pas autorisé à fermer ce ticket");
            }
        }

        ticket.setStatus(STATUS_TICKET.FERME);
        ticket.setDateCloture(new Date());

        Ticket saved = ticketRepository.save(ticket);

        if (currentUser.getRole() == ROLE.TECHNICIEN && ticket.getAssignedTo() != null) {
            Technicien technicien = technicienRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Technicien non trouvé"));

            int score = scoreCalculatorService.calculateScore(saved, technicien);
            System.out.println("Score calculated for technician " + technicien.getNom() + ": " + score);
        }

        try {
            if (saved.getCreatedBy() != null) {
                notificationService.notifyStatusChanged(saved.getCreatedBy(), saved.getIdTicket(), "FERME");
            }

            NotificationMessage msg = new NotificationMessage(
                    "TICKET_CLOSED", "Ticket Fermé",
                    "Le ticket " + saved.getTitre() + " a été fermé par " + currentUser.getNom(),
                    saved.getIdTicket(), "SYSTEM", "INFO"
            );
            notificationService.notifyAdmin(msg);
        } catch (Exception e) {
            System.err.println("Notification failed: " + e.getMessage());
        }

        return ticketConverter.entityToDto(saved);
    }

    @Override
    @Transactional
    public DemandePrixResponse sendDemandePrix(String ticketId, DemandePrixRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé: " + ticketId));

        List<Utilisateur> technicians = userRepository.findByRole(ROLE.TECHNICIEN);

        if (technicians.isEmpty()) {
            throw new RuntimeException("Aucun technicien disponible dans le système");
        }

        ticket.setDemandePrixSent(true);
        ticket.setDemandePrixDeadline(request.getDeadline());
        ticket.setInterestedTechnicians(new ArrayList<>());
        ticket.setDeclinedTechnicians(new ArrayList<>());
        ticketRepository.save(ticket);

        int notifiedCount = 0;
        for (Utilisateur tech : technicians) {
            try {
                emailService.sendDemandePrixNotification(
                        tech.getEmail(),
                        ticket.getTitre(),
                        ticket.getIdTicket(),
                        request.getDeadline(),
                        request.getMessage()
                );
                notifiedCount++;
            } catch (Exception e) {
                System.err.println("Failed to send email to: " + tech.getEmail());
            }
        }

        return DemandePrixResponse.builder()
                .ticketId(ticketId)
                .ticketTitle(ticket.getTitre())
                .deadline(request.getDeadline())
                .sent(true)
                .techniciansNotified(notifiedCount)
                .message("Demande de prix envoyée à " + notifiedCount + " techniciens")
                .build();
    }

    @Override
    @Transactional
    public void technicianInterest(String ticketId, TechnicianInterestRequest request, String email) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé: " + ticketId));

        if (ticket.getDemandePrixDeadline() != null && ticket.getDemandePrixDeadline().before(new Date())) {
            throw new RuntimeException("Le délai de réponse est dépassé");
        }

        Utilisateur technician = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Technicien non trouvé"));

        boolean alreadyResponded = (ticket.getInterestedTechnicians() != null && ticket.getInterestedTechnicians().contains(technician)) ||
                (ticket.getDeclinedTechnicians() != null && ticket.getDeclinedTechnicians().contains(technician));

        if (alreadyResponded) {
            throw new RuntimeException("Vous avez déjà répondu à cette demande");
        }

        if (request.isInterested()) {
            if (ticket.getInterestedTechnicians() == null) {
                ticket.setInterestedTechnicians(new ArrayList<>());
            }
            ticket.getInterestedTechnicians().add(technician);

            try {
                emailService.sendInterestConfirmation(email, ticket.getTitre(), true);
            } catch (Exception e) {
                System.err.println("Failed to send confirmation email");
            }
        } else {
            if (ticket.getDeclinedTechnicians() == null) {
                ticket.setDeclinedTechnicians(new ArrayList<>());
            }
            ticket.getDeclinedTechnicians().add(technician);

            try {
                emailService.sendInterestConfirmation(email, ticket.getTitre(), false);
            } catch (Exception e) {
                System.err.println("Failed to send confirmation email");
            }
        }

        ticketRepository.save(ticket);
    }

    @Override
    public List<InterestedTechnicianDTO> getInterestedTechnicians(String ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé: " + ticketId));

        if (ticket.getInterestedTechnicians() == null || ticket.getInterestedTechnicians().isEmpty()) {
            return new ArrayList<>();
        }

        List<InterestedTechnicianDTO> result = new ArrayList<>();
        for (Utilisateur tech : ticket.getInterestedTechnicians()) {
            InterestedTechnicianDTO dto = new InterestedTechnicianDTO();
            dto.setId(tech.getId());
            dto.setNom(tech.getNom());
            dto.setPrenom(tech.getPrenom());
            dto.setEmail(tech.getEmail());
            dto.setTelephone(tech.getTelephone());

            if (tech instanceof Technicien) {
                Technicien t = (Technicien) tech;
                dto.setSpecialite(t.getSpecialite());
            }

            result.add(dto);
        }

        return result;
    }

    @Override
    public boolean hasTechnicianResponded(String ticketId, String email) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé: " + ticketId));

        Utilisateur technician = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Technicien non trouvé"));

        boolean isInterested = ticket.getInterestedTechnicians() != null && ticket.getInterestedTechnicians().contains(technician);
        boolean isDeclined = ticket.getDeclinedTechnicians() != null && ticket.getDeclinedTechnicians().contains(technician);

        return isInterested || isDeclined;
    }
}