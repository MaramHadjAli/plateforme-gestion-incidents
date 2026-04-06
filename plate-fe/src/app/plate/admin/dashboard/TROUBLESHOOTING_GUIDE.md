# 🔍 GUIDE DE DÉPANNAGE - LA CHARTE NE S'AFFICHE PAS

**Date:** 5 Avril 2026  
**Status:** Diagnostic en cours

---

## ⚠️ PROBLÈME
```
La charte cinématique n'est pas visible sur le dashboard
Même après actualisation (F5)
```

---

## 🔧 SOLUTIONS À ESSAYER (dans l'ordre)

### ✅ ÉTAPE 1: VÉRIFIER LA CONSOLE D'ERREURS (URGENT)

**Action:**
1. Appuyer **F12** (DevTools)
2. Aller dans l'onglet **Console**
3. Chercher les messages **rouges** (erreurs)

**Que chercher:**
```
❌ "CinematicHybridChartComponent is not defined"
❌ "Cannot find module..."
❌ "TypeError: Cannot read property..."
❌ Autres erreurs rouges
```

**À faire:**
- Prendre une capture d'écran des erreurs
- Nous rapporter EXACTEMENT ce qui est écrit
- C'est crucial pour diagnostiquer!

---

### ✅ ÉTAPE 2: VÉRIFIER LA STRUCTURE HTML

**Action:**
1. F12 (DevTools)
2. Onglet **Elements**
3. Chercher: `<app-cinematic-hybrid-chart>`

**Que chercher:**
```
✅ Balise <app-cinematic-hybrid-chart> présente
✅ Avec ses inputs configurés
✅ Avec ses events connectés
```

**Si absent:**
```
❌ Signifie: le template HTML n'a pas été mis à jour correctement
```

---

### ✅ ÉTAPE 3: VÉRIFIER LES DONNÉES

**Action dans la Console (F12):**
```javascript
// Copier/coller dans la console:
let component = ng.probe(document.querySelector('app-admin-dashboard')).componentInstance;
console.log('Stats:', component.stats);
console.log('Cinematic segments:', component.cinematicChartSegments);
```

**Que chercher:**
```
✅ stats contient des données
✅ cinematicChartSegments a 4 éléments
✅ Chaque segment a value > 0
```

**Si cinematicChartSegments est vide:**
```
❌ Les données ne sont pas chargées
❌ updateCinematicChartData() n'a pas été appelée
```

---

### ✅ ÉTAPE 4: VÉRIFIER LE COMPOSANT CINÉMATIQUE

**Action:**
1. Vérifier que 3 fichiers existent:
   ```
   ✓ cinematic-hybrid-chart.component.ts
   ✓ cinematic-hybrid-chart.component.html
   ✓ cinematic-hybrid-chart.component.css
   ```

2. Vérifier qu'aucun a d'erreur de syntaxe

**Si fichier manquant:**
```
❌ Recréer le fichier manquant
```

---

### ✅ ÉTAPE 5: HARD REFRESH DU NAVIGATEUR

**Action:**
1. Appuyer **Ctrl+Shift+R** (Windows/Linux)
   OU **Cmd+Shift+R** (Mac)
2. Attendre 5 secondes
3. Vérifier si la charte apparaît

**Pourquoi:**
```
❌ Cache navigateur peut être le problème
✅ Hard refresh efface le cache
```

---

### ✅ ÉTAPE 6: REDÉMARRER LE SERVEUR

**Action:**
1. Arrêter `npm start` (Ctrl+C dans le terminal)
2. Attendre 2 secondes
3. Lancer `npm start` à nouveau
4. Attendre le message "Application running"
5. Ouvrir le dashboard

**Pourquoi:**
```
❌ Compilateur Angular peut avoir un problème
✅ Redémarrer le serveur récompile tout
```

---

## 🎯 CHECKLIST À VÉRIFIER

### Dans les fichiers:

- [ ] `admin-dashboard.component.ts` a l'import du composant
  ```typescript
  import { CinematicHybridChartComponent } from '...cinematic-hybrid-chart.component';
  ```

- [ ] Composant dans imports du décorateur
  ```typescript
  imports: [..., CinematicHybridChartComponent]
  ```

- [ ] Propriété cinematicChartSegments existe
  ```typescript
  cinematicChartSegments: {...}[] = [...]
  ```

- [ ] loadStats() appelle updateCinematicChartData()
  ```typescript
  this.updateCinematicChartData();
  ```

- [ ] updateCinematicChartData() existe
  ```typescript
  private updateCinematicChartData(): void { ... }
  ```

### Dans le HTML:

- [ ] Section ajoutée
  ```html
  <section class="cinematic-section">
    <app-cinematic-hybrid-chart ...></app-cinematic-hybrid-chart>
  </section>
  ```

### Dans le CSS:

- [ ] Styles ajoutés
  ```css
  .cinematic-section { ... }
  ```

---

## 📊 CAS COURANTS & SOLUTIONS

### Cas 1: "Je vois une section vide/blanche"
```
Signifie: La section existe mais le graphique ne rend pas
Solution:
1. Vérifier console (F12) pour erreurs Canvas
2. Vérifier que cinematic-hybrid-chart.component.ts existe
3. Vérifier que le canvas peut se dessiner
```

### Cas 2: "Je vois l'erreur: Component not defined"
```
Signifie: L'import est manquant ou incorrect
Solution:
1. Vérifier que cinematic-hybrid-chart.component.ts existe
2. Vérifier le chemin d'import
3. Vérifier que le composant est dans imports[]
```

### Cas 3: "Je vois la section mais le graphique est gris/vide"
```
Signifie: Le composant rend mais pas de données
Solution:
1. Vérifier console pour erreurs
2. Vérifier que stats contient des données
3. Vérifier que updateCinematicChartData() est appelée
4. Vérifier que cinematicChartSegments a des valeurs
```

### Cas 4: "Je vois la section mais rien ne s'affiche"
```
Signifie: Données vides ou valeurs = 0
Solution:
1. Créer quelques incidents d'abord
2. Vérifier que le dashboard charge des données
3. Vérifier que ticketsByPriority contient des nombres
```

---

## 🚀 DIAGNOSTIC RAPIDE

**Copier/coller dans la console (F12):**

```javascript
// Check 1: Component instance
let dash = ng.probe(document.querySelector('app-admin-dashboard')).componentInstance;
console.log('=== COMPONENT CHECK ===');
console.log('Stats loaded:', !!dash.stats);
console.log('Stats data:', dash.stats?.ticketsByPriority);
console.log('Cinematic segments:', dash.cinematicChartSegments);

// Check 2: DOM element
let element = document.querySelector('app-cinematic-hybrid-chart');
console.log('=== DOM CHECK ===');
console.log('Component in DOM:', !!element);
console.log('Parent section exists:', !!document.querySelector('.cinematic-section'));

// Check 3: Canvas
let canvas = document.querySelector('canvas');
console.log('=== CANVAS CHECK ===');
console.log('Canvas exists:', !!canvas);
console.log('Canvas width:', canvas?.width);
console.log('Canvas height:', canvas?.height);
```

**Copier le résultat complet et nous le rapporter!**

---

## 📞 INFORMATIONS À FOURNIR

Pour qu'on puisse vous aider, envoyez:

1. **Screenshot de la console (F12) avec erreurs**
2. **Résultat du diagnostic rapide ci-dessus**
3. **Réponses:**
   - [ ] Avez-vous appuyé sur F5?
   - [ ] Avez-vous redémarré npm start?
   - [ ] Avez-vous un Ctrl+Shift+R (hard refresh)?
   - [ ] Voyez-vous des erreurs rouges dans la console?
   - [ ] La section "Analyse Cinématique" est-elle visible (même vide)?

---

## ✅ PROCHAINES ÉTAPES

### Immédiatement:
1. ✅ Appuyer **F12** pour ouvrir DevTools
2. ✅ Aller à l'onglet **Console**
3. ✅ Chercher les erreurs rouges
4. ✅ Nous rapporter exactement ce qui est écrit

### Puis:
1. ✅ Essayer **Ctrl+Shift+R** (hard refresh)
2. ✅ Redémarrer `npm start`
3. ✅ Copier/coller le diagnostic dans la console
4. ✅ Nous rapporter les résultats

---

**Status: 🔍 EN DIAGNOSTIC**

**👉 Ouvrir F12 et chercher les erreurs rouges dans la Console!**

🎬 **Nous allons trouver le problème ensemble!** 🎬

