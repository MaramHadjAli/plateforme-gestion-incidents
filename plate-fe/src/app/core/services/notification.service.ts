import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export type ToastType = 'success' | 'error' | 'warning' | 'info';

export interface Toast {
  id: string;
  message: string;
  type: ToastType;
  duration?: number;
  dismissible?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private toasts$ = new BehaviorSubject<Toast[]>([]);
  private toastCounter = 0;

  constructor() {}

  getToasts(): Observable<Toast[]> {
    return this.toasts$.asObservable();
  }

  success(message: string, duration: number = 3000): string {
    return this.showToast(message, 'success', duration);
  }

  error(message: string, duration: number = 5000): string {
    return this.showToast(message, 'error', duration);
  }

  warning(message: string, duration: number = 4000): string {
    return this.showToast(message, 'warning', duration);
  }

  info(message: string, duration: number = 3000): string {
    return this.showToast(message, 'info', duration);
  }

  private showToast(message: string, type: ToastType, duration: number): string {
    const id = `toast-${++this.toastCounter}`;
    const toast: Toast = {
      id,
      message,
      type,
      duration,
      dismissible: true
    };

    const currentToasts = this.toasts$.value;
    this.toasts$.next([...currentToasts, toast]);

    if (duration > 0) {
      setTimeout(() => this.dismissToast(id), duration);
    }

    return id;
  }

  dismissToast(id: string): void {
    const currentToasts = this.toasts$.value;
    this.toasts$.next(currentToasts.filter(t => t.id !== id));
  }

  clearAll(): void {
    this.toasts$.next([]);
  }
}

