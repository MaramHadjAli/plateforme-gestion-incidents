import { Routes } from '@angular/router';
import { TicketListComponent } from './plate/technicien/ticket-list/ticket-list.component';
import { CreateTicketComponent } from './plate/demandeur/create-ticket/create-ticket.component';

export const routes: Routes = [
  { path: '', redirectTo: '/ticket-list', pathMatch: 'full' },
  { path: 'ticket-list', component: TicketListComponent },
  { path: 'create-ticket', component: CreateTicketComponent },
  { path: 'dashboard', component: TicketListComponent },
  { path: 'equipements', component: TicketListComponent },
  { path: 'maintenance', component: TicketListComponent },
  { path: 'classement', component: TicketListComponent },
  { path: 'profile', component: TicketListComponent },
  { path: 'my-tickets', component: TicketListComponent },
  { path: 'badges', component: TicketListComponent },
  { path: 'settings', component: TicketListComponent },
  { path: '**', redirectTo: '/ticket-list' }
];