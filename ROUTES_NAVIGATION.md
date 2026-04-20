# 🗺️ Routes & Navigation Guide

## 📱 Application Routes

### Authentication Routes (Publiques)
```
/home                    → Page d'accueil
/login                   → Connexion
/register                → Inscription
/forgot-password         → Mot de passe oublié
```

### Profile & Settings Routes (Protégées)
```
/profile                 → 👤 Page Profil (NEW)
/settings                → ⚙️ Page Paramètres (UPDATED)
```

### Admin Routes (Protégées)
```
/dashboard               → 📊 Tableau de bord admin
/admin/dashboard         → 📊 Tableau de bord admin (alternative)
```

### User Routes (Protégées)
```
/create-ticket           → 🎫 Créer un ticket
/ticket-list             → 📋 Liste des tickets
/my-tickets              → 🎫 Mes tickets
```

### Other Routes (Protégées)
```
/equipements             → 🔧 Gestion des équipements
/maintenance             → 🔨 Maintenance
/classement              → 📊 Classement
/badges                  → 🏅 Badges
```

### Default Route
```
/                        → Redirection vers /home
/**                      → Redirection vers /home (routes invalides)
```

---

## 🔐 Protection des Routes

### AuthGuard (Authentification requise)
Routes protégées pour tous les utilisateurs connectés:
```typescript
/profile
/settings
/create-ticket
/ticket-list
/my-tickets
/equipements
/maintenance
/classement
/badges
```

### AdminGuard (Rôle Admin requis)
Routes protégées pour les administrateurs:
```typescript
/dashboard
/admin/dashboard
```

---

## 🧭 Navigation Frontend

### Depuis le Code TypeScript

#### Naviguer vers Profil
```typescript
this.router.navigate(['/profile']);
```

#### Naviguer vers Paramètres
```typescript
this.router.navigate(['/settings']);
```

#### Avec Paramètres
```typescript
this.router.navigate(['/profile'], {
  queryParams: { tab: 'edit' }
});
```

### Depuis les Templates HTML

#### Lien simple
```html
<a routerLink="/profile">Mon Profil</a>
<a routerLink="/settings">Paramètres</a>
```

#### Lien avec classe active
```html
<a routerLink="/profile" 
   routerLinkActive="active">
   Mon Profil
</a>
```

#### Lien programmatique
```html
<button (click)="goToProfile()">
  Aller au Profil
</button>
```

---

## 📊 Structure des Routes

```
ApplicationRoutes
├── Public Routes
│   ├── /                    (redirect to /home)
│   ├── /home                (HomeComponent)
│   ├── /login               (LoginComponent)
│   ├── /register            (RegisterComponent)
│   └── /forgot-password     (ForgotPasswordComponent)
│
├── Protected Routes (AuthGuard)
│   ├── /profile             (ProfileComponent)          ✨ NEW
│   ├── /settings            (SettingsComponent)         ✏️ UPDATED
│   ├── /create-ticket       (CreateTicketComponent)
│   ├── /ticket-list         (TicketListComponent)
│   ├── /my-tickets          (TicketListComponent)
│   ├── /equipements         (TicketListComponent)
│   ├── /maintenance         (TicketListComponent)
│   ├── /classement          (ClassementComponent)
│   └── /badges              (TicketListComponent)
│
├── Admin Routes (AdminGuard)
│   ├── /dashboard           (AdminDashboardComponent)
│   └── /admin/dashboard     (AdminDashboardComponent)
│
└── Catch-all
    └── /**                  (redirect to /home)
```

---

## 🔄 Navigation Flow

### Utilisateur Non Authentifié
```
Any Protected Route
        ↓
  AuthGuard Check
        ↓
   Not Authenticated
        ↓
  Redirect to /login
```

### Utilisateur Authentifié (Non-Admin)
```
/profile               → ProfileComponent ✅
/settings              → SettingsComponent ✅
/dashboard             → AdminGuard Check ✗
                       → Redirect to /home
```

### Utilisateur Authentifié (Admin)
```
/profile               → ProfileComponent ✅
/settings              → SettingsComponent ✅
/dashboard             → AdminDashboardComponent ✅
/admin/dashboard       → AdminDashboardComponent ✅
```

---

## 🛣️ Routes Configuration

### Code Source
**Location:** `src/app/app.routes.ts`

```typescript
export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  
  // Protected routes
  { path: 'ticket-list', component: TicketListComponent, canActivate: [AuthGuard] },
  { path: 'create-ticket', component: CreateTicketComponent, canActivate: [AuthGuard] },
  { path: 'dashboard', component: AdminDashboardComponent, canActivate: [AdminGuard] },
  { path: 'admin/dashboard', component: AdminDashboardComponent, canActivate: [AdminGuard] },
  
  // New routes
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'settings', component: SettingsComponent, canActivate: [AuthGuard] },
  
  // Catch-all
  { path: '**', redirectTo: '/home' }
];
```

---

## 🔐 Guards

### AuthGuard
**Location:** `src/app/core/guard/auth.guard.ts`

```typescript
Vérifie:
✓ Token JWT présent
✓ Token non expiré
✓ Utilisateur authentifié

Redirection: /login si non authentifié
```

### AdminGuard
**Location:** `src/app/core/guard/admin.guard.ts`

```typescript
Vérifie:
✓ Token JWT présent
✓ Rôle = ADMIN

Redirection: /home si non admin
```

---

## 📍 Navigation Communes

### De la Page Profil
```
Lien vers Paramètres
  → Click "⚙️ Paramètres"
  → Navigate to /settings
```

### De la Page Paramètres
```
Lien vers Profil
  → Click "Modifier le profil"
  → Navigate to /profile
```

### Après Déconnexion
```
User logs out from /settings
  → Clear auth tokens
  → Navigate to /home
```

### Après Suppression de Compte
```
User deletes account
  → Clear auth tokens
  → Navigate to /home
```

### Après Expiration de Session
```
Any protected route
  → Token expired
  → AuthGuard redirects to /login
```

---

## 🌍 Deep Linking

### Links avec Paramètres
```
/profile?tab=edit          → Profil en mode édition
/profile?tab=view          → Profil en mode lecture
/settings?section=security → Paramètres section sécurité
```

### Implementation
```typescript
// Dans le composant
ngOnInit() {
  this.route.queryParams.subscribe(params => {
    if (params['tab'] === 'edit') {
      this.toggleEditMode();
    }
  });
}
```

---

## 📱 Mobile Navigation

### Bottom Navigation (Exemple)
```
┌─────────────────────────────┐
│  Page Content               │
│                             │
├─────────────────────────────┤
│ Home │ Tickets │ Profile    │
│      │         │            │
└─────────────────────────────┘
```

### Routes Mobiles
```
/                    → Home
/ticket-list         → Mes Tickets
/profile             → Mon Profil
/settings            → Paramètres
```

---

## 🎯 URL Shortcuts

### Raccourcis Clavier
```
Alt+P              → /profile (si implémenté)
Alt+S              → /settings (si implémenté)
Alt+H              → /home
```

### QR Codes
```
Profil:     https://app.example.com/profile
Paramètres: https://app.example.com/settings
```

---

## 📊 Route Statistics

| Route | Component | Auth | Status |
|-------|-----------|------|--------|
| /home | HomeComponent | ✗ | ✅ |
| /login | LoginComponent | ✗ | ✅ |
| /profile | ProfileComponent | ✅ | ✅ NEW |
| /settings | SettingsComponent | ✅ | ✅ UPDATED |
| /create-ticket | CreateTicketComponent | ✅ | ✅ |
| /ticket-list | TicketListComponent | ✅ | ✅ |
| /dashboard | AdminDashboardComponent | ADMIN | ✅ |

---

## 🔗 Related Routes

### Profile Related
```
/profile               → Voir/Editer profil
/settings              → Paramètres utilisateur
/settings?section=security → Changer mot de passe
/settings?section=preferences → Préférences
```

### Ticket Related
```
/create-ticket         → Créer un nouveau ticket
/ticket-list           → Voir tous les tickets
/my-tickets            → Mes tickets
```

### Admin Related
```
/dashboard             → Tableau de bord
/admin/dashboard       → Tableau de bord alternatif
```

---

## ✅ Navigation Checklist

- [x] Routes configurées dans app.routes.ts
- [x] AuthGuard implémenté
- [x] AdminGuard implémenté
- [x] Redirection par défaut (/home)
- [x] Catch-all route configurée
- [x] Profil route ajoutée
- [x] Paramètres route mise à jour
- [x] Imports de composants
- [x] RouterModule importé
- [x] NavBar navigable

---

## 🚀 Routes Prêtes

```
✅ Public Routes      → Working
✅ Protected Routes   → Working (avec AuthGuard)
✅ Admin Routes       → Working (avec AdminGuard)
✅ Profile Route      → NEW ✨
✅ Settings Route     → UPDATED ✏️
✅ Error Handling     → Working
✅ Deep Linking       → Supported
✅ Mobile Navigation  → Responsive
```

---

**Dernière mise à jour:** 2026-04-20  
**Routes Total:** 17+ routes  
**Statut:** ✅ CONFIGURÉ


