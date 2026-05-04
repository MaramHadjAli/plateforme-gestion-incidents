import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProfileComponent } from './profile.component';
import { ProfileService } from './profile.service';
import { NotificationService } from '../../core/services/notification.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;
  let profileService: jasmine.SpyObj<ProfileService>;
  let notificationService: jasmine.SpyObj<NotificationService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const profileServiceSpy = jasmine.createSpyObj('ProfileService', [
      'getCurrentUser',
      'updateProfile'
    ]);
    const notificationServiceSpy = jasmine.createSpyObj('NotificationService', [
      'success',
      'error',
      'warning'
    ]);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ProfileComponent],
      providers: [
        { provide: ProfileService, useValue: profileServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    profileService = TestBed.inject(ProfileService) as jasmine.SpyObj<ProfileService>;
    notificationService = TestBed.inject(NotificationService) as jasmine.SpyObj<NotificationService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;

    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load user profile on init', () => {
    const mockProfile = {
      id: 1,
      nom: 'John Doe',
      email: 'john@example.com',
      telephone: '+216 12 345 678',
      role: 'DEMANDEUR'
    };

    profileService.getCurrentUser.and.returnValue(of(mockProfile));
    component.ngOnInit();

    expect(profileService.getCurrentUser).toHaveBeenCalled();
    expect(component.userProfile).toEqual(mockProfile);
  });

  it('should handle error when loading profile', () => {
    profileService.getCurrentUser.and.returnValue(throwError(() => new Error('Error')));
    component.ngOnInit();

    expect(notificationService.error).toHaveBeenCalledWith('Impossible de charger le profil');
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should toggle edit mode', () => {
    expect(component.isEditing).toBe(false);
    component.toggleEditMode();
    expect(component.isEditing).toBe(true);
  });
});

