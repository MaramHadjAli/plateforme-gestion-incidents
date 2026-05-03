package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.enicarthage.plate_be.entities.Feedback;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, String> {
    
    Optional<Feedback> findByTicketIdTicket(String ticketId);
    
    boolean existsByTicketIdTicket(String ticketId);
    
    List<Feedback> findByTicketAssignedToId(Long technicianId);
    
    @Query("SELECT AVG(f.note) FROM Feedback f WHERE f.ticket.assignedTo.id = :technicianId")
    Double getAverageNoteByTechnicienId(@Param("technicianId") Long technicianId);
}
