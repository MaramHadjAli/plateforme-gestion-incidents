import { PrioriteTicket } from "./priority-ticket.enum";

export interface TicketResponse {
  idTicket: string;
  titre: string;
  description: string;
  dateCreation: Date | string;
  dateCloture?: Date | string | null;
  dateLimite: Date | string;
  priorite: PrioriteTicket | string;
  status: string;
  demandeurNom: string;
  demandeurEmail?: string;
  technicienNom?: string;
  technicienId?: number;
  idSalle?: string;
  nomSalle?: string;
  idEquipement?: string;
  nomEquipement?: string;
}