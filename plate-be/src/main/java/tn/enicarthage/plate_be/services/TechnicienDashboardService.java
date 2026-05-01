package tn.enicarthage.plate_be.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import tn.enicarthage.plate_be.dtos.MaintenanceDTO;
import tn.enicarthage.plate_be.dtos.RecentTicketSummary;
import tn.enicarthage.plate_be.dtos.TechnicienDashboardDTO;

import tn.enicarthage.plate_be.entities.STATUS_TICKET;
import tn.enicarthage.plate_be.entities.STATUS_MAINTENANCE;
import tn.enicarthage.plate_be.entities.Technicien;
import tn.enicarthage.plate_be.entities.Ticket;

import tn.enicarthage.plate_be.repositories.MaintenancePreventiveRepository;
import tn.enicarthage.plate_be.repositories.TechnicienRepository;
import tn.enicarthage.plate_be.repositories.TicketRepository;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TechnicienDashboardService {

    private final TechnicienRepository technicienRepository;
    private final TicketRepository ticketRepository;
    private final MaintenancePreventiveRepository maintenanceRepository;

    public TechnicienDashboardDTO getDashboard(String email) {

        Technicien tech = technicienRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Technicien non trouvé: " + email));

        Long id = tech.getId();

        List<Ticket> allTickets = ticketRepository.findByAssignedTo_Id(id);

        long enCours = allTickets.stream()
                .filter(t -> STATUS_TICKET.EN_COURS.equals(t.getStatus()))
                .count();

        long resolus = allTickets.stream()
                .filter(t -> STATUS_TICKET.RESOLU.equals(t.getStatus()))
                .count();

        long enRetard = allTickets.stream()
                .filter(t -> t.getDateLimiteReparation() != null
                        && t.getDateLimiteReparation().before(new Date())
                        && !STATUS_TICKET.RESOLU.equals(t.getStatus())
                        && !STATUS_TICKET.FERME.equals(t.getStatus()))
                .count();

        double avgDays = allTickets.stream()
                .filter(t -> STATUS_TICKET.RESOLU.equals(t.getStatus())
                        && t.getDateCloture() != null
                        && t.getDateCreation() != null)
                .mapToLong(t ->
                        (t.getDateCloture().getTime() - t.getDateCreation().getTime())
                                / (1000 * 60 * 60 * 24)
                )
                .average()
                .orElse(0.0);

        List<RecentTicketSummary> recents = allTickets.stream()
                .sorted(Comparator.comparing(Ticket::getDateCreation,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(t -> new RecentTicketSummary(
                        t.getIdTicket(),
                        t.getTitre(),
                        t.getPriorite() != null ? t.getPriorite().name() : null,
                        t.getStatus() != null ? t.getStatus().name() : null,
                        t.getDateCreation() != null
                                ? t.getDateCreation().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .toString()
                                : null,
                        tech.getNom()
                ))
                .collect(Collectors.toList());

        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 30);
        Date in30Days = cal.getTime();

        List<MaintenanceDTO> prochaines = maintenanceRepository
                .findByDateRange(now, in30Days).stream()
                .filter(m -> m.getStatus() == STATUS_MAINTENANCE.PLANIFIEE)
                .map(m -> {
                    MaintenanceDTO dto = new MaintenanceDTO();
                    dto.setIdMaintenance(m.getIdMaintenance());
                    dto.setDateProgramme(m.getDateProgramme());
                    dto.setFrequence(m.getFrequence() != null ? m.getFrequence().name() : null);
                    dto.setDescription(m.getDescription());
                    dto.setStatus(m.getStatus());

                    if (m.getEquipement() != null) {
                        dto.setIdEquipement(m.getEquipement().getIdEquipement());
                        dto.setNomEquipement(m.getEquipement().getNomEquipement());
                    }

                    dto.setEnRetard(
                            m.getStatus() == STATUS_MAINTENANCE.PLANIFIEE &&
                                    m.getDateProgramme() != null &&
                                    m.getDateProgramme().before(new Date())
                    );

                    return dto;
                })
                .collect(Collectors.toList());

        return new TechnicienDashboardDTO(
                tech.getNom(),
                tech.getSpecialite(),
                tech.getTotalPoints(),
                tech.getNoteMoyenne(),
                allTickets.size(),
                enCours,
                resolus,
                enRetard,
                avgDays,
                recents,
                prochaines
        );
    }
}