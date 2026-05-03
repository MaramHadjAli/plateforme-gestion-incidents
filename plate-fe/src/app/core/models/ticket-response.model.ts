export interface TicketResponse {
  demandePrixSent: boolean;
  demandePrixDeadline: any;
  idTicket: string;
  titre: string;
  description: string;
  dateCreation: Date | string;
  dateCloture: Date | string | null;
  dateLimite: Date | string;
  priorite: string;
  status: string;
  demandeurNom: string;
  demandeurEmail: string;
  technicienNom: string | null;
  technicienId: number | null;
}