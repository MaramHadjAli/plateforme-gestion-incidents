import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SidebarComponent } from '../../shared/components/sidebar.component';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, SidebarComponent],
  template: `
    <div class="admin-layout">
      <app-sidebar></app-sidebar>
      <main class="admin-content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    .admin-layout {
      display: flex;
      flex-direction: row;
      min-height: 100vh;
      background: #f8fbff;
    }

    .admin-content {
      flex: 1;
      height: 100vh;
      overflow-y: auto;
      padding: 0;
    }

    :host-context(.dark) .admin-layout {
      background: #0f172a;
    }
  `]
})
export class AdminLayoutComponent {}
