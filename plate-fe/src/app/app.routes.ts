import { Routes } from '@angular/router';
import { TicketListComponent } from './plate/technicien/ticket-list/ticket-list.component';

export const routes: Routes = [
  { path: '', redirectTo: 'ticket-list', pathMatch: 'full' },
  { path: 'ticket-list', component: TicketListComponent },
  { path: '**', redirectTo: 'ticket-list' },
];
