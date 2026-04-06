import { Injectable } from '@angular/core';

export interface Toast {
  id: string;
  message: string;
  type: 'success' | 'error' | 'info' | 'warning';
  duration?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {

  toasts: Toast[] = [];
  private counter = 0;

  show(message: string, type: 'success' | 'error' | 'info' | 'warning' = 'info', duration: number = 3000): string {
    const id = `toast-${++this.counter}`;
    this.toasts.push({ id, message, type, duration });

    if (duration > 0) {
      setTimeout(() => this.dismiss(id), duration);
    }

    return id;
  }

  dismiss(id: string): void {
    this.toasts = this.toasts.filter((toast) => toast.id !== id);
  }
}
