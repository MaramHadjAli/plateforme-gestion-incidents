package tn.enicarthage.plate_be.services;

import java.time.Duration;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.dtos.AdminDashboardStats;
import tn.enicarthage.plate_be.dtos.RecentTicketSummary;
import tn.enicarthage.plate_be.entities.PRIORITE_TICKET;
import tn.enicarthage.plate_be.entities.ROLE;
import tn.enicarthage.plate_be.entities.STATUS_TICKET;
import tn.enicarthage.plate_be.entities.Ticket;
import tn.enicarthage.plate_be.repositories.TicketRepository;
import tn.enicarthage.plate_be.repositories.UserRepository;


@Service
public class AdminDashboardService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public AdminDashboardService(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    public AdminDashboardStats getStats() {
        long totalTickets = ticketRepository.count();
        long openTickets = ticketRepository.countByStatus(STATUS_TICKET.OUVERT);
        long inProgressTickets = ticketRepository.countByStatus(STATUS_TICKET.EN_COURS);
        long resolvedTickets = ticketRepository.countByStatus(STATUS_TICKET.RESOLU);
        long overdueTickets = ticketRepository.countByDateLimiteReparationBeforeAndStatusNot(
                java.util.Date.from(java.time.Instant.now()),
                STATUS_TICKET.RESOLU
        );
        long totalUsers = userRepository.count();
        long totalTechnicians = userRepository.countByRole(ROLE.TECHNICIEN);
        long activeTechnicians = userRepository.findAll().stream()
                .filter(u -> u.getRole() == ROLE.TECHNICIEN && u.isActive())
                .count();

        // Calcul du MTTR (Mean Time To Resolve)
        List<Ticket> resolvedList = ticketRepository.findAll().stream()
                .filter(t -> t.getStatus() == STATUS_TICKET.RESOLU && t.getDateCreation() != null && t.getDateCloture() != null)
                .collect(Collectors.toList());

        String mttr = "0h 0m";
        if (!resolvedList.isEmpty()) {
            long totalMinutes = resolvedList.stream()
                    .mapToLong(t -> Duration.between(t.getDateCreation().toInstant(), t.getDateCloture().toInstant()).toMinutes())
                    .sum();
            long avgMinutes = totalMinutes / resolvedList.size();
            long hours = avgMinutes / 60;
            long mins = avgMinutes % 60;
            mttr = hours + "h " + mins + "m";
        }

        Map<String, Long> ticketsByStatus = new HashMap<>();
        for (STATUS_TICKET status : STATUS_TICKET.values()) {
            ticketsByStatus.put(status.name(), ticketRepository.countByStatus(status));
        }

        Map<String, Long> ticketsByPriority = new HashMap<>();
        for (PRIORITE_TICKET priority : PRIORITE_TICKET.values()) {
            ticketsByPriority.put(priority.name(), ticketRepository.countByPriorite(priority));
        }

        List<RecentTicketSummary> recentTickets = ticketRepository.findAll().stream()
                .sorted(Comparator.comparing(Ticket::getDateCreation, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(5)
                .map(ticket -> new RecentTicketSummary(
                        ticket.getIdTicket(),
                        ticket.getTitre(),
                        ticket.getPriorite() != null ? ticket.getPriorite().name() : null,
                        ticket.getStatus() != null ? ticket.getStatus().name() : null,
                        ticket.getDateCreation() != null ? ticket.getDateCreation().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString() : null,
                        ticket.getAssignedTo() != null ? ticket.getAssignedTo().getNom() : "Non assigné"
                ))
                .collect(Collectors.toList());

        AdminDashboardStats stats = new AdminDashboardStats();
        stats.setTotalTickets(totalTickets);
        stats.setOpenTickets(openTickets);
        stats.setInProgressTickets(inProgressTickets);
        stats.setResolvedTickets(resolvedTickets);
        stats.setOverdueTickets(overdueTickets);
        stats.setTotalUsers(totalUsers);
        stats.setTotalTechnicians(totalTechnicians);
        stats.setTicketsByStatus(ticketsByStatus);
        stats.setTicketsByPriority(ticketsByPriority);
        stats.setRecentTickets(recentTickets);
        stats.setMeanTimeToResolve(mttr);
        stats.setActiveTechnicians(activeTechnicians);

        return stats;
    }
}
