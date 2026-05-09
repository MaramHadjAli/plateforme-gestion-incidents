# 🔧 Diagnostic du Problème d'Écran Blanc

## 📋 Checklist de Diagnostic Rapide

### Étape 1: Vérifier les logs du navigateur
```
F12 → Console (Ctrl+Shift+K)
```

**Ce que vous DEVRIEZ voir:**
```
✓ AppComponent constructor initialized
✓ AppComponent ngOnInit started
✓ Flowbite initialized
```

**Si vous voyez des erreurs:**
1. Notez l'EXACT message d'erreur
2. Cherchez "Cannot match any routes" → Problème de routing
3. Cherchez "cannot find module" → Import manquant
4. Cherchez "NullInjectorError" → Service non fourni

---

### Étape 2: Tester les routes directement

Essayez d'aller à ces URLs dans le navigateur:

| URL | Résultat attendu | Si écran blanc |
|-----|------------------|---|
| `http://localhost:4200` | Redirection vers `/login` | Guard bloque? |
| `http://localhost:4200/login` | Page de login s'affiche | Composant standalone cassé |
| `http://localhost:4200/register` | Page d'inscription | Composant standalone cassé |
| `http://localhost:4200/home` | Page d'accueil + guard | Non authentifié = redirection login |

---

### Étape 3: Vérifier le DOM

Dans F12 → Elements (Ctrl+Shift+C):

```html
<body>
  <app-root>
    <!-- DEVRAIT voir: -->
    <div class="bg-gray-50 dark:bg-gray-900 min-h-screen flex flex-col">
      <app-toast-container></app-toast-container>
      <main class="flex-1">
        <router-outlet><!-- LOGIN PAGE ou autre composant --></router-outlet>
      </main>
    </div>
  </app-root>
</body>
```

**Si le DOM est vide:**
- App n'a pas bootstrappé correctement
- main.ts a échoué silencieusement

---

### Étape 4: Vérifier Network (requêtes HTTP)

F12 → Network:

1. **Application JS bundle**: Cherchez `main-*.js`
   - ✅ Status 200 = téléchargé correctement
   - ❌ Status 404 = fichier manquant

2. **Requêtes API**: 
   - Vérifiez si `/api/auth/*` est appelée
   - Vérifiez les CORS errors

3. **Ressources statiques** (`/logos/*`, `favicon.ico`)
   - ❌ 404 sur images = peut briser le rendu

---

## 🛠️ Solutions Appliquées

### ✅ Fix #1: HomeComponent est maintenant `standalone: true`
```typescript
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  standalone: true,  // ← FIXED
  imports: [CommonModule]
})
```

### ✅ Fix #2: AuthModule vidé (conflit résolu)
```typescript
@NgModule({
  imports: [CommonModule]
  // Composants auth ne sont PLUS déclarés ici
  // Ils sont importés directement via les routes standalone
})
```

### ✅ Fix #3: Debug tracing activé
```typescript
provideRouter(routes, withDebugTracing())
// Affichera tous les événements de routing dans la console
```

### ✅ Fix #4: Logging dans AppComponent
```typescript
ngOnInit(): void {
  console.log('✓ AppComponent ngOnInit started');
  // Voir les logs pour identifier les erreurs
}
```

---

## 🚀 Après les Fixes - Testez

### 1. Arrêtez et relancez le serveur
```bash
npm stop  # Arrêtez ng serve
npm start # Relancez avec: ng serve --poll=2000
```

### 2. Nettoyez le cache
```bash
# Chrome/Edge: 
#  - F12 → Application → Clear storage → Clear site data
# Ou: Ctrl+Shift+Del → Cookies et cache
```

### 3. Vérifiez la console
```
F12 → Console
```

**Vous devriez voir:**
```
Angular is running in development mode. Call enableDebugTools with an instance of an application from the browser console to get debugging tools.
✓ AppComponent constructor initialized
✓ AppComponent ngOnInit started
✓ Flowbite initialized
[Navigation]: Redirecting to /login
```

---

## 🔍 Si Ça Marche TOUJOURS PAS

### Debug supplémentaire: Ajouter dans `main.ts`
```typescript
import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

console.log('🔵 Starting Angular bootstrap...');

bootstrapApplication(AppComponent, appConfig)
  .then(() => console.log('✅ Bootstrap successful'))
  .catch((err) => {
    console.error('❌ Bootstrap failed:', err);
    document.body.innerHTML = `<h1 style="color:red; padding:20px">Bootstrap Error:<br>${err.message}</h1>`;
  });
```

### Vérifier l'intercepteur
Si vous voyez un message "Interceptor error":
```typescript
// src/app/core/interceptors/auth.interceptor.ts
// Vérifiez qu'il ne bloque pas les requêtes cruciales
```

### Vérifier les Guards
Les guards pourraient rediriger silencieusement:
```typescript
// Si AuthGuard bloque /login lui-même, c'est une boucle infinie
// Vérifiez: canActivate devrait TOUJOURS permettre /login et /register
```

---

## 📊 State Check

Si rien ne marche, créez `/app/debug.component.ts`:

```typescript
import { Component, OnInit } from '@angular/core';
import { AuthService } from './core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-debug',
  standalone: true,
  template: `
    <pre style="background: #f0f0f0; padding: 20px; white-space: pre-wrap;">
{{ debugInfo }}
    </pre>
  `
})
export class DebugComponent implements OnInit {
  debugInfo = '';

  constructor(private auth: AuthService, private router: Router) {}

  ngOnInit() {
    this.debugInfo = JSON.stringify({
      isAuthenticated: this.auth.isAuthenticated(),
      token: this.auth.getToken(),
      currentUser: this.auth.getUserData(),
      currentUrl: this.router.url,
      routes: 'Check app.routes.ts'
    }, null, 2);
  }
}
```

Puis allez à `http://localhost:4200/debug` (après l'ajouter aux routes)

---

## 📞 Résumé

| Problème | Solution | Fichier |
|----------|----------|---------|
| HomeComponent ne load pas | `standalone: true` | `auth/home/home.component.ts` |
| Conflit module/standalone | AuthModule vidé | `auth/auth.module.ts` |
| Pas de routing logs | `withDebugTracing()` | `app.config.ts` |
| Erreurs silencieuses | Logging ajouté | `app.component.ts` + `main.ts` |


