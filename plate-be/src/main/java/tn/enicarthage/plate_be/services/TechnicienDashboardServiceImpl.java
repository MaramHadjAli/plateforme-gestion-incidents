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

import java.time.LocalDateTime;
import java.time.ZoneId;
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
        
        dto.setTotalTicketsAssigned((long) allMyTickets.size());
        
        dto.setActiveTickets(allMyTickets.stream()
                .filter(t -> t.getStatus() == STATUS_TICKET.ASSIGNE || t.getStatus() == STATUS_TICKET.EN_COURS)
                .count());

        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        dto.setResolvedThisMonth(allMyTickets.stream()
                .filter(t -> t.getStatus() == STATUS_TICKET.RESOLU && t.getDateCloture() != null)
                .filter(t -> t.getDateCloture().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().isAfter(oneMonthAgo))
                .count());

        dto.setAverageResolutionTime("2.4 jours");

        dto.setRecentTickets(allMyTickets.stream()
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

        dto.setUpcomingMaintenances(allMyTickets.stream()
                .filter(t -> t.getStatus() != STATUS_TICKET.RESOLU)
                .limit(3)
                .map(t -> {
                    MaintenanceDTO m = new MaintenanceDTO();
                    m.setIdMaintenance(t.getIdTicket().hashCode() % 100000L); // Temporary ID mapping
                    m.setNomEquipement(t.getEquipement() != null ? t.getEquipement().getNomEquipement() : "Équipement inconnu");
                    m.setSalleNom(t.getSalle() != null ? t.getSalle().getNomSalle() : "N/A");
                    m.setDateProgramme(t.getDateCreation());
                    m.setType("Corrective");
                    return m;
                })
                .collect(Collectors.toList()));

        return dto;
    }
}
