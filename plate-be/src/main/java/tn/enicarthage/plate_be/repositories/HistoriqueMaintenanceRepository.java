package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicarthage.plate_be.entities.HistoriqueMaintenance;

import java.util.List;

@Repository
public interface HistoriqueMaintenanceRepository extends JpaRepository<HistoriqueMaintenance, Long> {
    
    List<HistoriqueMaintenance> findByEquipementIdEquipementOrderByDateExecutionDesc(String idEquipement);
    
    List<HistoriqueMaintenance> findByTechnicienIdOrderByDateExecutionDesc(Long technicienId);
}
