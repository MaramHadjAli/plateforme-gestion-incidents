export interface RecentTicketSummary {
  idTicket: string;
  titre: string;
  priorite: string;
  status: string;
  dateCreation: string;
  technicienAssignee: string;
}

export interface MaintenanceDTO {
  idMaintenance: number;
  dateProgramme: string;
  frequence: string;
  description: string;
  status: string;
  idEquipement: string;
  nomEquipement: string;
  enRetard: boolean;
}

export interface TechnicienDashboardDTO {
  nomTechnicien: string;
  specialite: string;
  totalPoints: number;
  noteMoyenne: number;
  totalTicketsAssignes: number;
  ticketsEnCours: number;
  ticketsResolus: number;
  ticketsEnRetard: number;
  tempsMoyenResolutionJours: number;
  ticketsRecents: RecentTicketSummary[];
  maintenancesProches: MaintenanceDTO[];
}
