package tn.enicarthage.plate_be.services;

import tn.enicarthage.plate_be.dtos.TechnicienRankingDTO;
import tn.enicarthage.plate_be.dtos.TechnicianScoreDTO;

import java.util.List;

public interface ClassementService {
    List<TechnicienRankingDTO> getGlobalRanking();
    TechnicianScoreDTO getTechnicianScoreDetails(String email);
}
