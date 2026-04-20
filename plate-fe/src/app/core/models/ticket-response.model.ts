import { PrioriteTicket } from "./priority-ticket.enum";

export interface TicketResponse {
  idTicket: string;
  titre: string;
  description: string;
  dateCreation: Date | string;
  dateLimite: Date | string;
  priorite: PrioriteTicket | string;
  status: string;
  demandeurNom: string;
}