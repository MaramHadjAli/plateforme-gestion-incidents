export interface RecentTicketSummary {
  idTicket: string;
  titre: string;
  priorite: string | null;
  statut: string | null;
  dateCreation: string | null;
  technicien: string | null;
}

export interface AdminDashboardStats {
  totalTickets: number;
  openTickets: number;
  inProgressTickets: number;
  resolvedTickets: number;
  overdueTickets: number;
  totalUsers: number;
  totalTechnicians: number;
  ticketsByStatus: Record<string, number>;
  ticketsByPriority: Record<string, number>;
  recentTickets: RecentTicketSummary[];
}

