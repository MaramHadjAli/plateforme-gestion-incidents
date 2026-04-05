import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { NotificationService } from './notification.service';

@Injectable({
  providedIn: 'root'
})
export class ErrorHandlerService {

  constructor(private notificationService: NotificationService) {}

  handleError(error: HttpErrorResponse | Error, context?: string): void {
    let message = 'Une erreur est survenue';

    if (error instanceof HttpErrorResponse) {
      switch (error.status) {
        case 0:
          message = 'Impossible de se connecter au serveur. Vérifiez votre connexion internet.';
          break;
        case 400:
          message = error.error?.message || 'Données invalides';
          break;
        case 401:
          message = 'Authentification requise';
          break;
        case 403:
          message = 'Accès refusé';
          break;
        case 404:
          message = 'Ressource non trouvée';
          break;
        case 409:
          message = error.error?.message || 'Conflit de données';
          break;
        case 500:
          message = 'Erreur serveur. Veuillez réessayer plus tard.';
          break;
        case 503:
          message = 'Service indisponible. Veuillez réessayer plus tard.';
          break;
        default:
          message = error.error?.message || `Erreur HTTP ${error.status}`;
      }
    } else if (error instanceof Error) {
      message = error.message || message;
    }

    if (context) {
      message = `${context}: ${message}`;
    }

    this.notificationService.error(message);
  }

  handleValidationError(errors: any): void {
    if (Array.isArray(errors)) {
      errors.forEach(error => {
        this.notificationService.error(error.message || error);
      });
    } else if (typeof errors === 'object') {
      Object.keys(errors).forEach(key => {
        const errorMsg = errors[key];
        if (Array.isArray(errorMsg)) {
          errorMsg.forEach(msg => {
            this.notificationService.error(msg);
          });
        } else {
          this.notificationService.error(errorMsg);
        }
      });
    } else {
      this.notificationService.error(String(errors));
    }
  }

  logError(error: any, context?: string): void {
    const timestamp = new Date().toISOString();
    const errorInfo = {
      timestamp,
      context,
      error: error instanceof Error ? error.message : String(error),
      stack: error instanceof Error ? error.stack : undefined
    };

    console.error('[Error Log]', errorInfo);

    // En production, vous pourriez envoyer cela à un service de monitoring
    // this.sendToMonitoringService(errorInfo);
  }
}

