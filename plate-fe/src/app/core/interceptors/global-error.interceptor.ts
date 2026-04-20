import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ToastService } from '../services/toast.service';
import { Router } from '@angular/router';

@Injectable()
export class GlobalErrorInterceptor implements HttpInterceptor {

  constructor(private toastService: ToastService, private router: Router) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMsg = '';
        
        if (error.error instanceof ErrorEvent) {
           // Client side error
           errorMsg = `Error: ${error.error.message}`;
           this.toastService.showError(errorMsg, 'Client Error');
        } else {
           // Server side error
           switch (error.status) {
             case 401:
               errorMsg = 'Session expired or unauthorized. Please login again.';
               this.toastService.showWarning(errorMsg, 'Unauthorized');
               this.router.navigate(['/login']);
               break;
             case 403:
               errorMsg = 'You do not have permission to perform this action.';
               this.toastService.showError(errorMsg, 'Forbidden Access');
               break;
             case 404:
               errorMsg = 'The requested resource could not be found.';
               this.toastService.showError(errorMsg, 'Not Found');
               break;
             case 500:
               errorMsg = error.error?.message || 'Internal Server Error occurred. Please try again later.';
               this.toastService.showError(errorMsg, 'Server Error');
               break;
             default:
               errorMsg = error.error?.message || `Unexpected error: ${error.message}`;
               this.toastService.showError(errorMsg, 'Error');
               break;
           }
        }
        
        console.error('Captured by GlobalErrorInterceptor:', error);
        return throwError(() => new Error(errorMsg));
      })
    );
  }
}
