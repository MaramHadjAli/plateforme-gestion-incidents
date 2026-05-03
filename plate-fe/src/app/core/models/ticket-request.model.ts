export interface TicketRequest {
  titre: string;
  description: string;
  priorite: string;
  dateLimite: Date | string;
  idSalle?: string;
  idEquipement?: string;
}
