package tn.enicarthage.plate_be.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.dtos.MaintenanceDTO;
import tn.enicarthage.plate_be.dtos.RecentTicketSummary;
import tn.enicarthage.plate_be.dtos.TechnicienDashboardDTO;
import tn.enicarthage.plate_be.entities.STATUS_TICKET;
import tn.enicarthage.plate_be.entities.Technicien;
import tn.enicarthage.plate_be.entities.Ticket;
import tn.enicarthage.plate_be.repositories.TechnicienRepository;
import tn.enicarthage.plate_be.repositories.TicketRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TechnicienDashboardServiceImpl implements TechnicienDashboardService {

    private final TicketRepository ticketRepository;
    private final TechnicienRepository technicienRepository;

    @Override
    public TechnicienDashboardDTO getDashboard(String email) {
        Technicien tech = technicienRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Technicien non trouvé"));

        List<Ticket> allMyTickets = ticketRepository.findByAssignedTo_Id(tech.getId());

        TechnicienDashboardDTO dto = new TechnicienDashboardDTO();
        
        // Stats Performance
        dto.setTotalPoints((long) tech.getTotalPoints());
        dto.setNoteMoyenne(tech.getNoteMoyenne());
        dto.setTicketsResolus((long) tech.getTicketsResolus());
        dto.setTotalTicketsAssignes((long) allMyTickets.size());
        
        // Tickets par status
        dto.setTicketsEnCours(allMyTickets.stream()
                .filter(t -> t.getStatus() == STATUS_TICKET.ASSIGNE || t.getStatus() == STATUS_TICKET.EN_COURS)
                .count());

        // Simulation de tickets en retard (pour la démo)
        dto.setTicketsEnRetard(allMyTickets.stream()
                .filter(t -> t.getStatus() != STATUS_TICKET.RESOLU && t.getStatus() != STATUS_TICKET.FERME)
                .filter(t -> t.getDateLimiteReparation() != null && t.getDateLimiteReparation().before(new java.util.Date()))
                .count());

        // Tickets Récents
        dto.setTicketsRecents(allMyTickets.stream()
                .sorted((a, b) -> {
                    if (a.getDateCreation() == null || b.getDateCreation() == null) return 0;
                    return b.getDateCreation().compareTo(a.getDateCreation());
                })
                .limit(5)
                .map(t -> new RecentTicketSummary(
                        t.getIdTicket(),
                        t.getTitre(),
                        t.getPriorite() != null ? t.getPriorite().name() : "MOYENNE",
                        t.getStatus() != null ? t.getStatus().name() : "OUVERT",
                        t.getDateCreation() != null ? t.getDateCreation().toString() : "",
                        tech.getNom()
                ))
                .collect(Collectors.toList()));

        // Maintenances Proches (On utilise les tickets assignés qui ne sont pas encore résolus)
        dto.setMaintenancesProches(allMyTickets.stream()
                .filter(t -> t.getStatus() != STATUS_TICKET.RESOLU && t.getStatus() != STATUS_TICKET.FERME)
                .limit(3)
                .map(t -> {
                    MaintenanceDTO m = new MaintenanceDTO();
                    m.setIdMaintenance(t.getIdTicket().hashCode() % 100000L);
                    m.setNomEquipement(t.getEquipement() != null ? t.getEquipement().getNomEquipement() : "Équipement inconnu");
                    m.setDescription(t.getDescription());
                    m.setFrequence(t.getPriorite() != null ? t.getPriorite().name() : "N/A");
                    m.setDateProgramme(t.getDateCreation());
                    m.setEnRetard(t.getDateLimiteReparation() != null && t.getDateLimiteReparation().before(new java.util.Date()));
                    return m;
                })
                .collect(Collectors.toList()));

        return dto;
    }
}
