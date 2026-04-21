# Guide d'Implémentation Complet - Corrections Appliquées

---

## 🔧 PARTIE 1: Backend (Spring Boot) - Profile Update Fix

### 1.1 Modification de l'Entité Utilisateur

**Fichier**: `Utilisateur.java`

**Changement**: Ajouter le champ d'avatar après le champ `telephone`

```java
@Getter
@Setter
@Column(length = 20)
private String telephone;

@Getter
@Setter
@Column(length = 500)  // URL can be long
private String avatarUrl;
```

**Migration SQL (si Flyway/Liquibase n'est pas utilisé)**:
```sql
ALTER TABLE utilisateur ADD COLUMN avatar_url VARCHAR(500) NULL;
```

---

### 1.2 Mise à Jour du DTO UserResponseDTO

**Fichier**: `UserResponseDTO.java`

**Avant**:
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String nom;
    private String email;
    private String telephone;
    private String role;
}
```

**Après**:
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String nom;
    private String email;
    private String telephone;
    private String role;
    private String avatarUrl;  // ✨ NOUVEAU
}
```

---

### 1.3 Mise à Jour du Service UserService

**Fichier**: `UserService.java`

#### Méthode 1: Mettre à jour getCurrentUserInfo()
```java
public UserResponseDTO getCurrentUserInfo() {
    Utilisateur user = getCurrentUser();
    return UserResponseDTO.builder()
            .id(user.getId())
            .nom(user.getNom())
            .email(user.getEmail())
            .telephone(user.getTelephone())
            .role(user.getRole().name())
            .avatarUrl(user.getAvatarUrl())  // ✨ NOUVEAU
            .build();
}
```

#### Méthode 2: Mettre à jour updateProfile()
```java
public UserResponseDTO updateProfile(UpdateProfileDTO updateDTO) {
    Utilisateur user = getCurrentUser();
    
    if (updateDTO.getNom() != null && !updateDTO.getNom().isBlank()) {
        user.setNom(updateDTO.getNom());
    }
    
    if (updateDTO.getTelephone() != null && !updateDTO.getTelephone().isBlank()) {
        user.setTelephone(updateDTO.getTelephone());
    }
    
    Utilisateur updated = userRepository.save(user);
    
    return UserResponseDTO.builder()
            .id(updated.getId())
            .nom(updated.getNom())
            .email(updated.getEmail())
            .telephone(updated.getTelephone())
            .role(updated.getRole().name())
            .avatarUrl(updated.getAvatarUrl())  // ✨ NOUVEAU
            .build();
}
```

#### Méthode 3: Mettre à jour getUserByEmail()
```java
public UserResponseDTO getUserByEmail(String email) {
    Utilisateur user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    
    return UserResponseDTO.builder()
            .id(user.getId())
            .nom(user.getNom())
            .email(user.getEmail())
            .telephone(user.getTelephone())
            .role(user.getRole().name())
            .avatarUrl(user.getAvatarUrl())  // ✨ NOUVEAU
            .build();
}
```

#### Méthode 4: Ajouter updateAvatar() - NOUVELLE
```java
public UserResponseDTO updateAvatar(String avatarUrl) {
    Utilisateur user = getCurrentUser();
    user.setAvatarUrl(avatarUrl);
    Utilisateur updated = userRepository.save(user);
    
    return UserResponseDTO.builder()
            .id(updated.getId())
            .nom(updated.getNom())
            .email(updated.getEmail())
            .telephone(updated.getTelephone())
            .role(updated.getRole().name())
            .avatarUrl(updated.getAvatarUrl())
            .build();
}
```

#### Méthode 5: Ajouter deleteAvatarUrl() - NOUVELLE
```java
public UserResponseDTO deleteAvatarUrl() {
    Utilisateur user = getCurrentUser();
    user.setAvatarUrl(null);
    Utilisateur updated = userRepository.save(user);
    
    return UserResponseDTO.builder()
            .id(updated.getId())
            .nom(updated.getNom())
            .email(updated.getEmail())
            .telephone(updated.getTelephone())
            .role(updated.getRole().name())
            .avatarUrl(updated.getAvatarUrl())
            .build();
}
```

---

### 1.4 Mise à Jour du Contrôleur UserController

**Fichier**: `UserController.java`

**Imports à ajouter**:
```java
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
```

**Ajouter ces endpoints**:

#### Endpoint 1: Upload Avatar
```java
@PostMapping("/avatar/upload")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
    try {
        // Vérifier la taille du fichier
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Fichier trop volumineux (max 5MB)"));
        }

        // Vérifier le type de fichier
        if (!file.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Le fichier doit être une image"));
        }

        // Créer le répertoire s'il n'existe pas
        Path uploadPath = Paths.get("uploads/avatars/");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Générer un nom de fichier unique
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Sauvegarder le fichier
        Files.write(filePath, file.getBytes());

        // Construire l'URL de l'avatar
        String avatarUrl = "/uploads/avatars/" + fileName;

        // Mettre à jour l'avatar dans la base de données
        UserResponseDTO updatedUser = userService.updateAvatar(avatarUrl);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Avatar uploadé avec succès");
        response.put("avatarUrl", avatarUrl);
        response.put("user", updatedUser);

        return ResponseEntity.ok(response);

    } catch (IOException e) {
        return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors de l'upload du fichier"));
    }
}
```

#### Endpoint 2: Delete Avatar
```java
@DeleteMapping("/avatar")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<?> deleteAvatar() {
    try {
        UserResponseDTO updatedUser = userService.deleteAvatarUrl();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Avatar supprimé avec succès");
        response.put("user", updatedUser);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erreur lors de la suppression de l'avatar"));
    }
}
```

---

## 🎨 PARTIE 2: Frontend (Angular) - Dark Mode Relocation

### 2.1 Mise à Jour ProfileComponent

**Fichier**: `profile.component.ts`

#### Avant:
```typescript
import { ThemeService } from '../../core/services/theme.service';

export class ProfileComponent implements OnInit, OnDestroy {
  isDarkMode = false;
  
  constructor(private themeService: ThemeService, ...) {}
  
  ngOnInit(): void {
    this.themeService.darkMode$
      .pipe(takeUntil(this.destroy$))
      .subscribe(isDark => {
        this.isDarkMode = isDark;
      });
  }
  
  toggleTheme(): void {
    this.themeService.toggleDarkMode();
  }
}
```

#### Après:
```typescript
// ❌ Supprimer l'import ThemeService
// ❌ Supprimer la propriété isDarkMode
// ❌ Supprimer la souscription dans ngOnInit()
// ❌ Supprimer la méthode toggleTheme()

export class ProfileComponent implements OnInit, OnDestroy {
  // Les autres propriétés restent intactes
  userProfile: UserProfile | null = null;
  isLoading = false;
  isEditing = false;
  editForm!: FormGroup;
  avatarPreview: string | null = null;
  // isDarkMode - ❌ SUPPRIMÉ
  
  // ...reste du code inchangé
}
```

### 2.2 Mise à Jour du Template ProfileComponent

**Fichier**: `profile.component.html`

#### Avant:
```html
<div class="profile-header">
  <h1>Mon Profil</h1>
  <div class="header-actions">
    <button
      class="btn-theme"
      (click)="toggleTheme()"
      title="Basculer mode sombre">
      {{ isDarkMode ? '☀️' : '🌙' }}
    </button>
    <button
      *ngIf="!isEditing"
      class="btn-edit"
      (click)="toggleEditMode()"
      [disabled]="isLoading">
      Modifier
    </button>
  </div>
</div>
```

#### Après:
```html
<div class="profile-header">
  <h1>Mon Profil</h1>
  <div class="header-actions">
    <!-- ❌ Bouton dark mode SUPPRIMÉ -->
    <button
      *ngIf="!isEditing"
      class="btn-edit"
      (click)="toggleEditMode()"
      [disabled]="isLoading">
      Modifier
    </button>
  </div>
</div>
```

---

### 2.3 Mise à Jour AppBarComponent

**Fichier**: `app-bar.component.ts`

#### Imports à ajouter:
```typescript
import { ThemeService } from '../../core/services/theme.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
```

#### Ajouter/Modifier la classe:
```typescript
export class AppBarComponent implements OnInit, OnDestroy {  // ✨ Ajouter OnDestroy
  // ...autres propriétés...
  isDarkMode = false;  // ✨ NOUVEAU
  private destroy$ = new Subject<void>();  // ✨ NOUVEAU

  constructor(
    private elRef: ElementRef,
    private router: Router,
    private authService: AuthService,
    private themeService: ThemeService  // ✨ NOUVEAU
  ) {
    // ...constructor code...
  }

  ngOnInit(): void {
    // ...existing user subscription...
    
    // ✨ Ajouter souscription au thème
    this.themeService.darkMode$
      .pipe(takeUntil(this.destroy$))
      .subscribe((isDark: boolean) => {
        this.isDarkMode = isDark;
      });
  }

  // ✨ AJOUTER ngOnDestroy
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // ...autres méthodes...

  // ✨ Ajouter méthode toggleTheme
  toggleTheme(): void {
    this.themeService.toggleDarkMode();
  }
}
```

---

### 2.4 Mise à Jour du Template AppBarComponent

**Fichier**: `app-bar.component.html`

#### Ajouter le bouton dark mode dans la navbar:
```html
<div class="app-navbar__actions">
  <!-- ✨ NOUVEAU: Bouton Dark Mode -->
  <button
    type="button"
    (click)="toggleTheme()"
    class="app-icon-btn"
    title="Basculer mode sombre"
    aria-label="Basculer mode sombre">
    {{ isDarkMode ? '☀️' : '🌙' }}
  </button>

  <!-- Autres boutons existants -->
  <div class="relative" data-search>
    <!-- ... -->
  </div>
  
  <!-- Notifications -->
  <div class="relative" data-notif>
    <!-- ... -->
  </div>
  
  <!-- Profil -->
  <div class="relative" data-profile>
    <!-- ... -->
  </div>

  <!-- Menu Mobile -->
  <button type="button" (click)="mobileOpen = !mobileOpen" class="lg:hidden app-icon-btn">
    ☰
  </button>
</div>
```

---

## 🧪 Tests de Validation

### Backend - Test Upload Avatar

```bash
curl -X POST http://localhost:8080/api/users/avatar/upload \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/image.jpg"
```

**Réponse attendue**:
```json
{
  "message": "Avatar uploadé avec succès",
  "avatarUrl": "/uploads/avatars/550e8400-e29b-41d4-a716-446655440000_image.jpg",
  "user": {
    "id": 1,
    "nom": "John Doe",
    "email": "john@example.com",
    "telephone": "+216 50 123 456",
    "role": "DEMANDEUR",
    "avatarUrl": "/uploads/avatars/550e8400-e29b-41d4-a716-446655440000_image.jpg"
  }
}
```

### Backend - Test Update Profile

```bash
curl -X PUT http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "John Updated",
    "telephone": "+216 50 654 321"
  }'
```

**Réponse attendue**:
```json
{
  "id": 1,
  "nom": "John Updated",
  "email": "john@example.com",
  "telephone": "+216 50 654 321",
  "role": "DEMANDEUR",
  "avatarUrl": "/uploads/avatars/550e8400-e29b-41d4-a716-446655440000_image.jpg"
}
```

### Frontend - Test Dark Mode

1. Naviguer vers `/profile`
2. Vérifier le dark mode toggle dans la navbar (pas dans le profil)
3. Cliquer sur le bouton 🌙/☀️
4. Vérifier que toute la page change de thème
5. Naviguer vers une autre page
6. Vérifier que le thème persiste
7. Rafraîchir la page
8. Vérifier que le thème est toujours appliqué

---

## 📦 Configuration Serveur (Optionnel)

### Application.properties - Configuration pour les uploads

```properties
# Uploads configuration
upload.dir=uploads/avatars/
server.servlet.multipart.max-file-size=5MB
server.servlet.multipart.max-request-size=5MB
```

### Spring Boot Config - Exposer le dossier uploads

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }
}
```

---

## ✅ Checklist de Déploiement

- [ ] Migration database pour ajouter colonne `avatar_url`
- [ ] Backend compilé avec succès
- [ ] Frontend compilé avec succès
- [ ] Dossier `uploads/avatars/` créé
- [ ] Tests upload avatar exécutés
- [ ] Tests update profile exécutés
- [ ] Tests dark mode exécutés
- [ ] Tests persistance du thème exécutés
- [ ] Vérifier les permissionsdu dossier uploads (775 ou 755)
- [ ] Backup de la base de données effectué

---

## 🐛 Dépannage

### Problème: Upload avatar échoue avec 404
**Solution**: Vérifier que le dossier `uploads/avatars/` existe et que l'application a les droits d'accès.

### Problème: Dark mode ne persiste pas
**Solution**: Vérifier que `localStorage` est activé dans le navigateur.

### Problème: Avatar URL retourne 404
**Solution**: Vérifier la configuration de `WebMvcConfigurer` et que le chemin est correct.

### Problème: Téléphone n'est pas mis à jour
**Solution**: Vérifier que le pattern regex accepte le format: `/^[0-9+\-\s()]*$/`

---

## 📚 Ressources

- [Spring Boot File Upload](https://spring.io/guides/gs/uploading-files/)
- [Angular OnDestroy Hook](https://angular.io/guide/lifecycle-hooks)
- [localStorage API](https://developer.mozilla.org/en-US/docs/Web/API/Window/localStorage)
- [TypeScript Subject](https://rxjs.dev/guide/subject)


