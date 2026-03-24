package tn.enicarthage.plate_be.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class AdminDashboardStats {
    private long totalTickets;
    private long totalUsers;
    private long totalTechnicians;
    private Map<String, Long> ticketsByStatus;

    public AdminDashboardStats() {}

    public AdminDashboardStats(long totalTickets, long totalUsers, long totalTechnicians, Map<String, Long> ticketsByStatus) {
        this.totalTickets = totalTickets;
        this.totalUsers = totalUsers;
        this.totalTechnicians = totalTechnicians;
        this.ticketsByStatus = ticketsByStatus;
    }

}
