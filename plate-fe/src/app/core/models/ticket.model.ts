export interface Ticket {
demandePrixSent: any;
    id: string;
    titre: string;
    description: string;
    priorite: string;
    statut: string;
    dateCreation: string;
    dateLimite: string;
    dateCloture: string | null;
    technicien: string;
    demandeur: string;
  }