package tn.enicarthage.plate_be.repositories;

import tn.enicarthage.plate_be.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, String> {

    Optional<Feedback> findByTicketIdTicket(String ticketId);

    @Query("SELECT f FROM Feedback f WHERE f.ticket.idTicket = :ticketId")
    Optional<Feedback> findByTicketId(@Param("ticketId") String ticketId);

    List<Feedback> findByDemandeurId(Long demandeurId);

    @Query("SELECT f FROM Feedback f WHERE f.ticket.assignedTo.id = :technicienId")
    List<Feedback> findByTechnicienId(@Param("technicienId") Long technicienId);

    @Query("SELECT AVG(f.note) FROM Feedback f WHERE f.ticket.assignedTo.id = :technicienId")
    Double getAverageNoteByTechnicienId(@Param("technicienId") Long technicienId);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Feedback f WHERE f.ticket.idTicket = :ticketId")
    boolean existsByTicketId(@Param("ticketId") String ticketId);
}