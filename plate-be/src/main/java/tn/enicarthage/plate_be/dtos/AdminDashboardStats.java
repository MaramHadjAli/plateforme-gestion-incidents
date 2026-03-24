package tn.enicarthage.plate_be.dtos;

import java.util.Map;

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

    public long getTotalTickets() { return totalTickets; }
    public void setTotalTickets(long totalTickets) { this.totalTickets = totalTickets; }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalTechnicians() { return totalTechnicians; }
    public void setTotalTechnicians(long totalTechnicians) { this.totalTechnicians = totalTechnicians; }

    public Map<String, Long> getTicketsByStatus() { return ticketsByStatus; }
    public void setTicketsByStatus(Map<String, Long> ticketsByStatus) { this.ticketsByStatus = ticketsByStatus; }
}
