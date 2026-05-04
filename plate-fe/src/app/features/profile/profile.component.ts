import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ProfileService, UserProfile, UpdateProfile } from './profile.service';
import { ToastService } from '../../core/services/toast.service';
import { AvatarService } from '../../core/services/avatar.service';
import { AuthService } from '../../core/services/auth.service';
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
    private toastService: ToastService,
    private avatarService: AvatarService,
    private authService: AuthService,
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
      prenom: ['', [Validators.required, Validators.minLength(2)]],
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
            prenom: profile.prenom,
            telephone: profile.telephone || ''
          });
          if (profile.avatarUrl) {
            this.avatarPreview = profile.avatarUrl.startsWith('http') ? profile.avatarUrl : `http://localhost:8080${profile.avatarUrl}`;
            this.authService.updateAvatarUrl(this.avatarPreview);
          } else {
            this.avatarPreview = null;
            this.authService.updateAvatarUrl(undefined);
          }
          this.authService.updateUserDetails(profile.nom, profile.telephone);
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Erreur lors du chargement du profil:', error);
          this.toastService.showError('Impossible de charger le profil');
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
        prenom: this.userProfile.prenom,
        telephone: this.userProfile.telephone || ''
      });
    }
  }

  saveProfile(): void {
    if (!this.editForm.valid) {
      this.toastService.showWarning('Veuillez corriger les erreurs du formulaire');
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
          this.toastService.showSuccess('Profil mis à jour avec succès', 'Succès');
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Erreur lors de la mise à jour du profil:', error);
          this.toastService.showError('Erreur lors de la mise à jour du profil');
          this.isLoading = false;
        }
      });
  }

  onAvatarSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      // Vérifier la taille du fichier (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        this.toastService.showError('La taille du fichier ne doit pas dépasser 5MB');
        return;
      }

      // Vérifier le type de fichier
      if (!file.type.startsWith('image/')) {
        this.toastService.showError('Veuillez sélectionner une image valide');
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
            this.toastService.showSuccess('Avatar mis à jour avec succès', 'Succès');
            if (response && response.avatarUrl) {
              const fullUrl = response.avatarUrl.startsWith('http') ? response.avatarUrl : `http://localhost:8080${response.avatarUrl}`;
              this.avatarPreview = fullUrl;
              this.authService.updateAvatarUrl(fullUrl);
            }
            this.loadUserProfile();
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Erreur lors de l\'upload de l\'avatar:', error);
            this.toastService.showError('Erreur lors de l\'upload de l\'avatar');
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
            this.authService.updateAvatarUrl(undefined);
            this.loadUserProfile();
            this.toastService.showSuccess('Avatar supprimé avec succès', 'Succès');
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Erreur lors de la suppression de l\'avatar:', error);
            this.toastService.showError('Erreur lors de la suppression de l\'avatar');
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


