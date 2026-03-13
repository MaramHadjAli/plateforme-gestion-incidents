package tn.enicarthage.plate_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.enicarthage.plate_be.entities.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, String>{

}
