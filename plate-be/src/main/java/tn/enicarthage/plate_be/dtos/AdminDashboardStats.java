package tn.enicarthage.plate_be.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class AdminDashboardStats {
    private long totalTickets;
    private long openTickets;
    private long inProgressTickets;
    private long resolvedTickets;
    private long overdueTickets;
    private long totalUsers;
    private long totalTechnicians;
    private Map<String, Long> ticketsByStatus;
    private Map<String, Long> ticketsByPriority;
    private List<RecentTicketSummary> recentTickets;

    public AdminDashboardStats() {}

    public AdminDashboardStats(
            long totalTickets,
            long openTickets,
            long inProgressTickets,
            long resolvedTickets,
            long overdueTickets,
            long totalUsers,
            long totalTechnicians,
            Map<String, Long> ticketsByStatus,
            Map<String, Long> ticketsByPriority,
            List<RecentTicketSummary> recentTickets
    ) {
        this.totalTickets = totalTickets;
        this.openTickets = openTickets;
        this.inProgressTickets = inProgressTickets;
        this.resolvedTickets = resolvedTickets;
        this.overdueTickets = overdueTickets;
        this.totalUsers = totalUsers;
        this.totalTechnicians = totalTechnicians;
        this.ticketsByStatus = ticketsByStatus;
        this.ticketsByPriority = ticketsByPriority;
        this.recentTickets = recentTickets;
    }

}


