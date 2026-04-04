import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { initFlowbite } from 'flowbite';
import { AppBarComponent } from './shared/app-bar/app-bar.component';
import { FooterComponent } from './shared/footer/footer.component';
import { ToastContainerComponent } from './shared/toast-container/toast-container.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    AppBarComponent,
    FooterComponent,
    ToastContainerComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'plate-fe';

  constructor(private router: Router) {}

  ngOnInit(): void {
    initFlowbite();
  }

  isAuthRoute(): boolean {
    const currentUrl = this.router.url;
    return (
      currentUrl.includes('/login') ||
      currentUrl.includes('/register') ||
      currentUrl.includes('/forgot-password')
    );
  }
}
