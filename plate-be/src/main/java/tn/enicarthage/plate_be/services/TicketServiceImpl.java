package tn.enicarthage.plate_be.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tn.enicarthage.plate_be.converters.TicketConverter;
import tn.enicarthage.plate_be.dtos.AdminDashboardStats;
import tn.enicarthage.plate_be.dtos.TicketCreateRequest;
import tn.enicarthage.plate_be.dtos.TicketCreateResponse;
import tn.enicarthage.plate_be.dtos.TicketRequestDTO;
import tn.enicarthage.plate_be.dtos.TicketResponseDTO;
import tn.enicarthage.plate_be.entities.*;
import tn.enicarthage.plate_be.repositories.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final SalleRepository salleRepository;
    private final EquipementRepository equipementRepository;
    private final UserRepository userRepository;
    private final DemandeurRepository demandeurRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final AdminDashboardService adminDashboardService;
    private final TicketConverter ticketConverter;

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        return authentication.getName();
    }

    private Demandeur resolveDemandeur() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Utilisateur user = userRepository.findByEmail(username).orElse(null);
            if (user instanceof Demandeur) {
                return (Demandeur) user;
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifie");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Utilisateur non autorise");
    }

    private PRIORITE_TICKET parsePriority(String value) {
        if (value == null) return null;
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if ("NORMAL".equals(normalized)) normalized = "NORMALE";
        try {
            return PRIORITE_TICKET.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private Date resolveDueDate(String requestedDate, PRIORITE_TICKET priority, Date baseDate) {
        if (requestedDate != null && !requestedDate.isBlank()) {
            LocalDate localDate = LocalDate.parse(requestedDate);
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        int hours = switch (priority) {
            case CRITIQUE -> 2;
            case HAUTE    -> 8;
            case NORMALE  -> 24;
            case FAIBLE   -> 72;
        };
        return Date.from(Instant.ofEpochMilli(baseDate.getTime()).plusSeconds(hours * 3600L));
    }

    private String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try {
            Path uploadDir = Paths.get("uploads", "tickets");
            Files.createDirectories(uploadDir);
            String original  = file.getOriginalFilename();
            String extension = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf('.')) : "";
            String filename = UUID.randomUUID() + extension;
            Path target = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), target);
            return target.toString().replace("\\", "/");
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors du stockage du fichier");
        }
    }

    private void broadcastAdminDashboard() {
        AdminDashboardStats stats = adminDashboardService.getStats();
        messagingTemplate.convertAndSend("/topic/admin-dashboard", stats);
    }

    // -------------------------------------------------------------------------
    // Interface implementations
    // -------------------------------------------------------------------------

    @Override
    public TicketCreateResponse createTicket(TicketCreateRequest request, MultipartFile file) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requete invalide");
        }

        PRIORITE_TICKET priority = parsePriority(request.getPriorite());
        if (priority == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Priorite invalide");
        }

        Salle salle = salleRepository.findById(request.getIdSalle())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Salle introuvable"));

        Equipement equipement = equipementRepository.findById(request.getIdEquipement())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Equipement introuvable"));

        if (equipement.getSalle() != null &&
                !equipement.getSalle().getIdSalle().equals(salle.getIdSalle())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Equipement non lie a la salle");
        }

        Demandeur demandeur = resolveDemandeur();
        Date now     = new Date();
        Date dueDate = resolveDueDate(request.getDateLimiteReparation(), priority, now);
        String photoUrl = storeFile(file);

        Ticket ticket = Ticket.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .priorite(priority)
                .status(STATUS_TICKET.OUVERT)
                .dateCreation(now)
                .dateLimiteReparation(dueDate)
                .salle(salle)
                .equipement(equipement)
                .typePanne(request.getTypePanne())
                .photoUrl(photoUrl)
                .createdBy(demandeur)
                .build();

        Ticket saved = ticketRepository.save(ticket);
        broadcastAdminDashboard();

        return new TicketCreateResponse(
                saved.getIdTicket(),
                saved.getDateCreation()          != null ? saved.getDateCreation().toInstant().toString()          : null,
                saved.getDateLimiteReparation()  != null ? saved.getDateLimiteReparation().toInstant().toString()  : null,
                saved.getStatus()                != null ? saved.getStatus().name()                                : null
        );
    }

    @Override
    public TicketResponseDTO createTicket(TicketRequestDTO dto) {
        Demandeur demandeur = demandeurRepository.findByEmail(getCurrentUserId());

        Ticket ticket = ticketConverter.dtoToEntity(dto);
        ticket.setDateCreation(new Date());
        ticket.setStatus(STATUS_TICKET.OUVERT);
        ticket.setCreatedBy(demandeur);

        return ticketConverter.entityToDto(ticketRepository.save(ticket));
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
                .orElseThrow(() -> new RuntimeException("Ticket introuvable : " + id));
        return ticketConverter.entityToDto(ticket);
    }

    @Override
    public List<TicketResponseDTO> getTicketsByIds(List<String> ids) {
        return ticketRepository.findAllById(ids)
                .stream()
                .map(ticketConverter::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ticket> getTicketsByCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé"));

        if (user instanceof Demandeur) {
            return ticketRepository.findByCreatedBy((Demandeur) user);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seuls les demandeurs peuvent voir leurs tickets");
    }
}