package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicarthage.plate_be.entities.TraceLogin;

public interface TraceLoginRepository extends JpaRepository<TraceLogin, Long> {
}