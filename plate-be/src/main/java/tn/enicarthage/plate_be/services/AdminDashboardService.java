package tn.enicarthage.plate_be.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.dtos.AdminDashboardStats;
import tn.enicarthage.plate_be.entities.ROLE;
import tn.enicarthage.plate_be.entities.STATUS_TICKET;
import tn.enicarthage.plate_be.repositories.TicketRepository;
import tn.enicarthage.plate_be.repositories.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public AdminDashboardStats getStats() {
        long totalTickets = ticketRepository.count();
        long totalUsers = userRepository.count();
        long totalTechnicians = userRepository.countByRole(ROLE.TECHNICIEN);

        Map<String, Long> ticketsByStatus = new HashMap<>();
        for (STATUS_TICKET status : STATUS_TICKET.values()) {
            ticketsByStatus.put(status.name(), ticketRepository.countByStatus(status));
        }

        return new AdminDashboardStats(
                totalTickets,
                totalUsers,
                totalTechnicians,
                ticketsByStatus
        );
    }
}
