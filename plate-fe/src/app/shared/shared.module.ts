import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppBarComponent } from './app-bar/app-bar.component';
import { FooterComponent } from './footer/footer.component';
import { ToastContainerComponent } from './toast-container/toast-container.component';



@NgModule({
  imports: [
    CommonModule,
    AppBarComponent,
    FooterComponent,
    ToastContainerComponent
  ],
  exports: [
    AppBarComponent,
    FooterComponent,
    ToastContainerComponent
  ]
})
export class SharedModule { }
