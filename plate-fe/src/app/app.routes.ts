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
import { AdminDashboardComponent } from './plate/admin/dashboard';
import { ProfileComponent } from './features/profile/profile.component';
import { SettingsComponent } from './auth/settings/settings.component';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'ticket-list', component: TicketListComponent, canActivate: [AuthGuard] },
  { path: 'create-ticket', component: CreateTicketComponent, canActivate: [AuthGuard] },
  { path: 'dashboard', component: AdminDashboardComponent, canActivate: [AdminGuard] },
  { path: 'admin/dashboard', component: AdminDashboardComponent, canActivate: [AdminGuard] },
  { path: 'equipements', component: TicketListComponent, canActivate: [AuthGuard] },
  { path: 'maintenance', component: TicketListComponent, canActivate: [AuthGuard] },
  { path: 'classement', component: ClassementComponent, canActivate: [AuthGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'my-tickets', component: TicketListComponent, canActivate: [AuthGuard] },
  { path: 'badges', component: TicketListComponent, canActivate: [AuthGuard] },
  { path: 'settings', component: SettingsComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '/home' }
];
