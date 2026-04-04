import { Component } from '@angular/core';
import { ToastService } from '../services/toast.service';
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-toast-container',
  templateUrl: './toast-container.component.html',
  styleUrls: ['./toast-container.component.css'],
  imports: [CommonModule],
  standalone : true
})
export class ToastContainerComponent {

  constructor(public toastService: ToastService) {}
}
