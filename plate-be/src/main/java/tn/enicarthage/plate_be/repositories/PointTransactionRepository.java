package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.enicarthage.plate_be.entities.PointTransaction;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, String>{

}
