package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.enicarthage.plate_be.entities.MaintenancePreventive;

import java.util.Date;
import java.util.List;

@Repository
public interface MaintenancePreventiveRepository extends JpaRepository<MaintenancePreventive, Long> {
    
    List<MaintenancePreventive> findByEquipement_IdEquipement(String idEquipement);
    
    @Query("SELECT m FROM MaintenancePreventive m WHERE m.dateProgramme BETWEEN :start AND :end")
    List<MaintenancePreventive> findByDateRange(@Param("start") Date start, @Param("end") Date end);
    
    @Query("SELECT m FROM MaintenancePreventive m WHERE m.dateProgramme < :now AND m.status = 'PLANIFIEE'")
    List<MaintenancePreventive> findOverdue(@Param("now") Date now);
}
