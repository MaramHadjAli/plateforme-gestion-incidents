package tn.enicarthage.plate_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechnicienDashboardDTO {

    private String nomTechnicien;
    private String specialite;
    private int totalPoints;
    private float noteMoyenne;

    private long ticketsAssignes;
    private long ticketsEnCours;
    private long ticketsResolus;
    private long ticketsEnRetard;

    private double tempsResolutionMoyenJours;

    private List<RecentTicketSummary> ticketsRecents;
    private List<MaintenanceDTO> prochainesMaintenances;
}