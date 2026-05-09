import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SettingsComponent } from './settings.component';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('SettingsComponent', () => {
  let component: SettingsComponent;
  let fixture: ComponentFixture<SettingsComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let notificationService: jasmine.SpyObj<NotificationService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', [
      'getUserData',
      'changePassword',
      'deleteAccount',
      'logout'
    ]);
    const notificationServiceSpy = jasmine.createSpyObj('NotificationService', [
      'success',
      'error',
      'warning'
    ]);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [SettingsComponent, HttpClientTestingModule],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    notificationService = TestBed.inject(NotificationService) as jasmine.SpyObj<NotificationService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;

    fixture = TestBed.createComponent(SettingsComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load user data on init', () => {
    const mockUser = {
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
      role: 'DEMANDEUR'
    };

    authService.getUserData.and.returnValue(mockUser);
    component.ngOnInit();

    expect(authService.getUserData).toHaveBeenCalled();
    expect(component.user).toEqual(mockUser);
  });

  it('should toggle password form', () => {
    expect(component.showPasswordForm).toBe(false);
    component.togglePasswordForm();
    expect(component.showPasswordForm).toBe(true);
  });

  it('should logout user', (done) => {
    component.logout();

    expect(authService.logout).toHaveBeenCalled();
    expect(notificationService.success).toHaveBeenCalledWith('Déconnexion réussie');

    setTimeout(() => {
      expect(router.navigate).toHaveBeenCalledWith(['/home']);
      done();
    }, 600);
  });
});
