export interface TechnicienDashboardDTO {
  totalPoints: number;
  noteMoyenne: number;
  ticketsResolus: number;
  totalTicketsAssignes: number;
  ticketsEnCours: number;
  ticketsEnRetard: number;
  ticketsRecents: Array<{
    idTicket: string;
    titre: string;
    priorite: string;
    status: string;
    dateCreation: string;
  }>;
  maintenancesProches: Array<{
    nomEquipement: string;
    description: string;
    frequence: string;
    dateProgramme: string;
    enRetard: boolean;
  }>;
}
