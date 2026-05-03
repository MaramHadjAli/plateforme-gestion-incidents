import { PrioriteTicket } from "./priority-ticket.enum";

export interface TicketRequest {
  titre: string;
  description: string;
  priorite: PrioriteTicket | string;
  dateLimite: Date | string;
  demandeurId: string;
  idSalle?: string;
  idEquipement?: string;
}
