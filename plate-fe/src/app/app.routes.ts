import { Routes } from '@angular/router';
import { TicketListComponent } from './plate/technicien/ticket-list/ticket-list.component';
import { CreateTicketComponent } from './plate/demandeur/create-ticket/create-ticket.component';
import { ClassementComponent } from './plate/technicien/classement/classement.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { ForgotPasswordComponent } from './auth/forgot-password/forgot-password.component';
import { HomeComponent } from './auth/home/home.component';
import { AuthGuard } from './core/guard/auth.guard';
import { AdminGuard } from './core/guard/admin.guard';
import { DemandeurGuard } from './core/guard/demandeur.guard';
import { AdminDashboardComponent } from './plate/admin/dashboard';
import { ProfileComponent } from './features/profile/profile.component';
import { SettingsComponent } from './auth/settings/settings.component';

import { MaintenanceListComponent } from './plate/admin/maintenance/maintenance-list.component';
import { SallesComponent } from './plate/admin/salles/salles.component';
import { EquipementsComponent } from './plate/admin/equipements/equipements.component';
import { TechnicienDashboardComponent } from './plate/technicien/dashboard/technicien-dashboard.component';
import { MaintenanceComponent } from './plate/technicien/maintenance/maintenance.component';
import { TechniciansListComponent } from './plate/admin/technicians-list/technicians-list.component';
import { ResetPasswordComponent } from './shared/reset-password/reset-password.component';
import { TicketResponseComponent } from './plate/technicien/ticket-response/ticket-response.component';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'ticket-list', component: TicketListComponent, canActivate: [AuthGuard] },
  { path: 'create-ticket', component: CreateTicketComponent, canActivate: [DemandeurGuard] },
  { path: 'dashboard', component: AdminDashboardComponent, canActivate: [AdminGuard] },
  { path: 'admin/dashboard', component: AdminDashboardComponent, canActivate: [AdminGuard] },
  { path: 'technicien/dashboard', component: TechnicienDashboardComponent, canActivate: [AuthGuard] },
  { path: 'admin/salles', component: SallesComponent, canActivate: [AdminGuard] },
  { path: 'admin/equipements', component: EquipementsComponent, canActivate: [AdminGuard] },
  { path: 'equipements', component: EquipementsComponent, canActivate: [AuthGuard] },
  { path: 'maintenance', component: MaintenanceListComponent, canActivate: [AuthGuard] },
  { path: 'create-ticket', component: CreateTicketComponent, canActivate: [AuthGuard, DemandeurGuard] },
  { path: 'dashboard', component: AdminDashboardComponent, canActivate: [AdminGuard] },
  { path: 'admin/dashboard', component: AdminDashboardComponent, canActivate: [AdminGuard] },
  { path: 'admin/salles', component: SallesComponent, canActivate: [AdminGuard] },
  { path: 'admin/equipements', component: EquipementsComponent, canActivate: [AdminGuard] },
  { path: 'technicien/dashboard', component: TechnicienDashboardComponent, canActivate: [AuthGuard] },
  { path: 'equipements', redirectTo: '/admin/equipements' },
  { path: 'maintenance', component: MaintenanceComponent, canActivate: [AuthGuard] },
  { path: 'classement', component: ClassementComponent, canActivate: [AuthGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'my-tickets', component: TicketListComponent, canActivate: [AuthGuard] },
  { path: 'badges', component: TicketListComponent, canActivate: [AuthGuard] },
  { path: 'settings', component: SettingsComponent, canActivate: [AuthGuard] },
  { path: 'settings', component: TicketListComponent, canActivate: [AuthGuard] },
  { path: 'technicians', component: TechniciansListComponent, canActivate: [AuthGuard] },
  { path: 'tickets/:id/respond', component: TicketResponseComponent },
  { path: '**', redirectTo: '/home' }
];
