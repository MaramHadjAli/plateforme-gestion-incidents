package tn.enicarthage.plate_be.services;

import tn.enicarthage.plate_be.dtos.TechnicienDashboardDTO;

public interface TechnicienDashboardService {
    TechnicienDashboardDTO getDashboard(String email);
}