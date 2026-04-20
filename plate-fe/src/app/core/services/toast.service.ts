import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class ToastService {

  constructor(private toastr: ToastrService) { }

  showSuccess(message: string, title?: string) {
    this.toastr.success(message, title, {
      progressBar: true,
      timeOut: 3000,
      positionClass: 'toast-bottom-right'
    });
  }

  showError(message: string, title?: string) {
    this.toastr.error(message, title, {
      progressBar: true,
      timeOut: 5000,
      positionClass: 'toast-bottom-right'
    });
  }

  showInfo(message: string, title?: string) {
    this.toastr.info(message, title, {
      progressBar: true,
      timeOut: 4000,
      positionClass: 'toast-bottom-right',
      toastClass: 'ngx-toastr bg-blue-600 text-white' // Custom tailwind classes
    });
  }

  showWarning(message: string, title?: string) {
    this.toastr.warning(message, title, {
      progressBar: true,
      timeOut: 4000,
      positionClass: 'toast-bottom-right'
    });
  }
}
