package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.enicarthage.plate_be.entities.PRIORITE_TICKET;
import tn.enicarthage.plate_be.entities.STATUS_TICKET;
import tn.enicarthage.plate_be.entities.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, String> {
    long countByStatus(STATUS_TICKET status);
    long countByPriorite(PRIORITE_TICKET priorite);
    long countByDateLimiteReparationBeforeAndStatusNot(java.util.Date date, STATUS_TICKET status);
}
