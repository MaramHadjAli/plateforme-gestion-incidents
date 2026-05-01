package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.enicarthage.plate_be.entities.MaintenancePreventive;
import tn.enicarthage.plate_be.entities.STATUS_MAINTENANCE;

import java.util.Date;
import java.util.List;

public interface MaintenancePreventiveRepository extends JpaRepository<MaintenancePreventive, Long> {

    List<MaintenancePreventive> findByEquipement_IdEquipement(String idEquipement);

    List<MaintenancePreventive> findByStatus(STATUS_MAINTENANCE status);

    @Query("SELECT m FROM MaintenancePreventive m WHERE m.dateProgramme BETWEEN :start AND :end")
    List<MaintenancePreventive> findByDateRange(
            @Param("start") Date start,
            @Param("end") Date end
    );

    @Query("SELECT m FROM MaintenancePreventive m WHERE m.dateProgramme <= :date AND m.status = tn.enicarthage.plate_be.entities.STATUS_MAINTENANCE.PLANIFIEE")
    List<MaintenancePreventive> findOverdue(@Param("date") Date date);
}