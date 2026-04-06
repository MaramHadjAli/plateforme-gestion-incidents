# 🎬 Integration Guide - Cinematic Hybrid Chart Dashboard

**Date:** Avril 2026  
**Version:** 1.0  
**Component:** CinematicHybridChartComponent

---

## 🎯 Guide d'Intégration au Dashboard Administrateur

### 1️⃣ Importer le Composant

**Fichier:** `admin-dashboard.component.ts`

```typescript
import { CinematicHybridChartComponent } from '../../../shared/components/cinematic-hybrid-chart.component';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule, 
    AdvancedDonutChartComponent,
    CinematicHybridChartComponent  // ← Ajouter ici
  ],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  // ...
}
```

### 2️⃣ Ajouter la Section dans le Template

**Fichier:** `admin-dashboard.component.html`

```html
<!-- Section Cinematic Hybrid Chart -->
<section class="cinematic-section">
  <app-cinematic-hybrid-chart
    [segments]="cinematicChartSegments"
    [title]="'Cinematic Incidents Analysis'"
    [subtitle]="'Real-time interactive exploration'"
    [enableSound]="true"
    [enableParticles]="true"
    [darkMode]="darkMode"
    (segmentSelected)="onCinematicSegmentSelected($event)"
    (segmentHovered)="onCinematicSegmentHovered($event)"
  ></app-cinematic-hybrid-chart>
</section>
```

### 3️⃣ Ajouter les Données et Handlers

**Fichier:** `admin-dashboard.component.ts`

```typescript
export class AdminDashboardComponent implements OnInit {
  // ... existing properties ...
  
  cinematicChartSegments: Segment[] = [];
  darkMode: boolean = true;

  ngOnInit(): void {
    // ... existing init code ...
    this.loadCinematicChartData();
  }

  private loadCinematicChartData(): void {
    // Convertir les données existantes au format cinematic
    this.cinematicChartSegments = this.prioritySeries.map(item => ({
      label: item.label,
      value: item.value,
      color: item.color,
      glowColor: this.adjustBrightness(item.color, 150)
    }));
  }

  onCinematicSegmentSelected(segment: any): void {
    console.log('Selected segment:', segment);
    // Ajouter votre logique de selection
    // ex: Afficher détails, filtrer données, etc.
  }

  onCinematicSegmentHovered(segment: any): void {
    console.log('Hovered segment:', segment);
    // Ajouter votre logique de hover
  }

  private adjustBrightness(hexColor: string, percent: number): string {
    const num = parseInt(hexColor.slice(1), 16);
    const amt = Math.round(2.55 * percent);
    const R = (num >> 16) + amt;
    const G = (num >> 8 & 0x00FF) + amt;
    const B = (num & 0x0000FF) + amt;

    return '#' + (
      0x1000000 + (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 +
      (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 +
      (B < 255 ? B < 1 ? 0 : B : 255)
    ).toString(16).slice(1);
  }
}
```

### 4️⃣ Ajouter les Styles CSS

**Fichier:** `admin-dashboard.component.css`

```css
/* Cinematic Chart Section */
.cinematic-section {
  display: grid;
  grid-column: 1 / -1;
  margin-top: 3rem;
  margin-bottom: 3rem;
  position: relative;
  z-index: 5;
}

/* Background accent */
.cinematic-section::before {
  content: '';
  position: absolute;
  inset: -2rem;
  background:
    radial-gradient(circle at 20% 50%, rgba(0, 255, 136, 0.05), transparent 50%),
    radial-gradient(circle at 80% 50%, rgba(0, 136, 255, 0.05), transparent 50%);
  border-radius: 2rem;
  z-index: -1;
  pointer-events: none;
}

@media (max-width: 1024px) {
  .cinematic-section {
    margin-top: 2rem;
    margin-bottom: 2rem;
  }

  .cinematic-section::before {
    inset: -1rem;
  }
}

@media (max-width: 768px) {
  .cinematic-section {
    margin-top: 1.5rem;
    margin-bottom: 1.5rem;
  }

  .cinematic-section::before {
    inset: -0.5rem;
    border-radius: 1rem;
  }
}
```

---

## 📊 Exemple Complet

### TypeScript Complet

```typescript
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CinematicHybridChartComponent } from '../../../shared/components/cinematic-hybrid-chart.component';

interface Segment {
  label: string;
  value: number;
  color: string;
  glowColor?: string;
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, CinematicHybridChartComponent],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  cinematicChartSegments: Segment[] = [];
  darkMode: boolean = true;

  ngOnInit(): void {
    this.loadCinematicChartData();
  }

  private loadCinematicChartData(): void {
    // Exemple avec données statiques
    this.cinematicChartSegments = [
      {
        label: 'Critical Priority',
        value: 45,
        color: '#ff0088',
        glowColor: '#ff4db3'
      },
      {
        label: 'High Priority',
        value: 32,
        color: '#00ff88',
        glowColor: '#00ffaa'
      },
      {
        label: 'Medium Priority',
        value: 28,
        color: '#0088ff',
        glowColor: '#00aaff'
      },
      {
        label: 'Low Priority',
        value: 15,
        color: '#ffaa00',
        glowColor: '#ffcc00'
      }
    ];
  }

  onCinematicSegmentSelected(segment: Segment): void {
    console.log('Selected:', segment);
    // Ajouter votre logique ici
  }

  onCinematicSegmentHovered(segment: Segment | null): void {
    console.log('Hovered:', segment);
    // Ajouter votre logique ici
  }
}
```

### HTML Complet

```html
<div class="admin-dashboard">
  <!-- Existing sections... -->

  <!-- Cinematic Chart Section -->
  <section class="cinematic-section">
    <app-cinematic-hybrid-chart
      [segments]="cinematicChartSegments"
      [title]="'Cinematic Incidents Analysis'"
      [subtitle]="'Interactive real-time visualization'"
      [enableSound]="true"
      [enableParticles]="true"
      [darkMode]="darkMode"
      (segmentSelected)="onCinematicSegmentSelected($event)"
      (segmentHovered)="onCinematicSegmentHovered($event)"
    ></app-cinematic-hybrid-chart>
  </section>
</div>
```

---

## 🎨 Personnalisation

### Changer les Couleurs

```typescript
cinematicChartSegments = [
  {
    label: 'Your Custom Label',
    value: 100,
    color: '#YOUR_HEX_COLOR',
    glowColor: '#YOUR_GLOW_COLOR'
  }
];
```

### Désactiver les Effets

```html
<!-- Désactiver son et particules -->
<app-cinematic-hybrid-chart
  [segments]="cinematicChartSegments"
  [enableSound]="false"
  [enableParticles]="false"
></app-cinematic-hybrid-chart>
```

### Mode Clair

```html
<!-- Activer le mode clair -->
<app-cinematic-hybrid-chart
  [segments]="cinematicChartSegments"
  [darkMode]="false"
></app-cinematic-hybrid-chart>
```

---

## 🧪 Test d'Intégration

### Checklist de Vérification

- [ ] Composant importe correctement
- [ ] Template affiche le composant
- [ ] Données chargent correctement
- [ ] Canvas render sans erreurs
- [ ] Animations fluides 60fps
- [ ] Interactions fonctionnent (hover, click)
- [ ] Son joue au clic
- [ ] Particules émises
- [ ] Responsive sur mobile
- [ ] Mode sombre/clair fonctionne

### Test dans DevTools

1. Ouvrir F12 (DevTools)
2. Console: Vérifier aucune erreur
3. Elements: Vérifier présence du canvas
4. Performance: Vérifier FPS 60
5. Network: Vérifier chargement des assets

---

## 📱 Responsive Design

### Desktop (>1024px)
- Largeur: Largeur complète
- Padding: 2rem
- Canvas: 500px max

### Tablet (768-1024px)
- Largeur: Adaptée
- Padding: 1.5rem
- Canvas: 400px max

### Mobile (<768px)
- Largeur: Full width
- Padding: 1rem
- Canvas: 300px max

---

## ⚡ Performance Optimization

### Conseils

1. **Canvas Size:** Adapter au viewport
2. **Particle Count:** Réduire sur mobile
3. **Animation Frame:** Utilise requestAnimationFrame
4. **GPU Acceleration:** Activé automatiquement
5. **Change Detection:** OnPush strategy

### Monitoring

```typescript
// Vérifier FPS dans console
let fps = 0;
let lastTime = performance.now();

function measureFPS() {
  const now = performance.now();
  fps = 1000 / (now - lastTime);
  console.log('FPS:', fps.toFixed(2));
  lastTime = now;
  requestAnimationFrame(measureFPS);
}

measureFPS();
```

---

## 🐛 Troubleshooting

### Canvas Blanc
```
❌ Problème: Canvas s'affiche vide
✅ Solution: Vérifier que ViewChild ref est correct
✅ Solution: Vérifier que segments n'est pas vide
✅ Solution: Vérifier console pour erreurs
```

### Pas d'Interaction
```
❌ Problème: Click/hover ne marche pas
✅ Solution: Vérifier que canvas a cursor: crosshair
✅ Solution: Vérifier que listeners sont attachés
✅ Solution: Vérifier devTools pour erreurs
```

### Animations Saccadées
```
❌ Problème: FPS < 60
✅ Solution: Désactiver enableParticles
✅ Solution: Réduire nombre de segments
✅ Solution: Vérifier pas de console.log en loop
```

### Son ne Joue pas
```
❌ Problème: AudioContext silencieux
✅ Solution: Vérifier enableSound = true
✅ Solution: Cliquer d'abord (autoplay policy)
✅ Solution: Vérifier console pour erreurs audio
```

---

## 📚 Documentation Complète

Pour plus de détails sur le composant:
👉 Consulter: `CINEMATIC_HYBRID_CHART_README.md`

---

## 🎉 Résumé

L'intégration du **Cinematic Hybrid Chart** au dashboard ajoute:

✨ **Visualisation Futuriste**
- Graphique hybride Pie/Donut
- Animations cinématiques fluides
- Effets neon et glow

🎯 **Interactions Avancées**
- Hover effects
- Click zoom
- Particle system
- Sound feedback

⚡ **Performance**
- 60fps constant
- GPU accelerated
- Canvas optimisé

♿ **Accessibilité**
- Dark mode
- prefers-reduced-motion
- Responsive design

---

*Guide d'intégration: Avril 2026*  
*Status: ✅ COMPLET*

