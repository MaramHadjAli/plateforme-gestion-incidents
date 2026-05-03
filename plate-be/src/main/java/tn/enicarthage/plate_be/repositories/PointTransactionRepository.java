package tn.enicarthage.plate_be.repositories;

import tn.enicarthage.plate_be.entities.PointTransaction;
import tn.enicarthage.plate_be.entities.Technicien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, String> {

    List<PointTransaction> findByTechnicienId(Long technicienId);

    List<PointTransaction> findByTechnicienOrderByDateAttributionDesc(Technicien technicien);

    @Query("SELECT SUM(p.quantite) FROM PointTransaction p WHERE p.technicien.id = :technicienId")
    Integer sumByTechnicienId(@Param("technicienId") Long technicienId);

    @Query("SELECT AVG(p.quantite) FROM PointTransaction p WHERE p.technicien.id = :technicienId")
    Double averageByTechnicienId(@Param("technicienId") Long technicienId);

    @Query("SELECT p FROM PointTransaction p WHERE p.technicien.id = :technicienId ORDER BY p.dateAttribution DESC")
    List<PointTransaction> findLatestByTechnicienId(@Param("technicienId") Long technicienId);
}