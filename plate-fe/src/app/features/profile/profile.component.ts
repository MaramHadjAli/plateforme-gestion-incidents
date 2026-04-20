import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ProfileService, UserProfile, UpdateProfile } from './profile.service';
import { NotificationService } from '../../core/services/notification.service';
import { AvatarService } from '../../core/services/avatar.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule]
})
export class ProfileComponent implements OnInit, OnDestroy {
  userProfile: UserProfile | null = null;
  isLoading = false;
  isEditing = false;
  editForm!: FormGroup;
  avatarPreview: string | null = null;
  private destroy$ = new Subject<void>();

  constructor(
    private profileService: ProfileService,
    private notificationService: NotificationService,
    private avatarService: AvatarService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    this.loadUserProfile();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  initForm(): void {
    this.editForm = this.fb.group({
      nom: ['', [Validators.required, Validators.minLength(2)]],
      telephone: ['', [Validators.pattern(/^[0-9+\-\s()]*$/)]]
    });
  }

  loadUserProfile(): void {
    this.isLoading = true;
    this.profileService.getCurrentUser()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (profile) => {
          this.userProfile = profile;
          this.editForm.patchValue({
            nom: profile.nom,
            telephone: profile.telephone || ''
          });
          if (profile.avatarUrl) {
            this.avatarPreview = profile.avatarUrl;
          }
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Erreur lors du chargement du profil:', error);
          this.notificationService.error('Impossible de charger le profil');
          this.isLoading = false;
          this.router.navigate(['/login']);
        }
      });
  }

  toggleEditMode(): void {
    this.isEditing = !this.isEditing;
    if (!this.isEditing && this.userProfile) {
      this.editForm.patchValue({
        nom: this.userProfile.nom,
        telephone: this.userProfile.telephone || ''
      });
    }
  }

  saveProfile(): void {
    if (!this.editForm.valid) {
      this.notificationService.warning('Veuillez corriger les erreurs du formulaire');
      return;
    }

    const updateData: UpdateProfile = this.editForm.value;
    this.isLoading = true;

    this.profileService.updateProfile(updateData)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.loadUserProfile();
          this.isEditing = false;
          this.notificationService.success('Profil mis à jour avec succès');
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Erreur lors de la mise à jour du profil:', error);
          this.notificationService.error('Erreur lors de la mise à jour du profil');
          this.isLoading = false;
        }
      });
  }

  onAvatarSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      // Vérifier la taille du fichier (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        this.notificationService.error('La taille du fichier ne doit pas dépasser 5MB');
        return;
      }

      // Vérifier le type de fichier
      if (!file.type.startsWith('image/')) {
        this.notificationService.error('Veuillez sélectionner une image valide');
        return;
      }

      // Afficher l'aperçu
      const reader = new FileReader();
      reader.onload = () => {
        this.avatarPreview = reader.result as string;
      };
      reader.readAsDataURL(file);

      // Upload l'avatar
      this.isLoading = true;
      this.avatarService.uploadAvatar(file)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response) => {
            this.notificationService.success('Avatar mis à jour avec succès');
            this.loadUserProfile();
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Erreur lors de l\'upload de l\'avatar:', error);
            this.notificationService.error('Erreur lors de l\'upload de l\'avatar');
            this.isLoading = false;
          }
        });
    }
  }

  deleteAvatar(): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer votre avatar?')) {
      this.isLoading = true;
      this.avatarService.deleteAvatar()
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.avatarPreview = null;
            this.loadUserProfile();
            this.notificationService.success('Avatar supprimé avec succès');
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Erreur lors de la suppression de l\'avatar:', error);
            this.notificationService.error('Erreur lors de la suppression de l\'avatar');
            this.isLoading = false;
          }
        });
    }
  }

  getRoleLabel(): string {
    if (!this.userProfile) return '';
    const roleMap: { [key: string]: string } = {
      'DEMANDEUR': 'Demandeur',
      'TECHNICIEN': 'Technicien',
      'ADMIN': 'Administrateur'
    };
    return roleMap[this.userProfile.role] || this.userProfile.role;
  }
}


