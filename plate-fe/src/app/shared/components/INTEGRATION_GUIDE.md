# 🚀 Advanced Donut Chart - Guide d'Intégration

## 📋 Table des matières

1. [Installation Rapide](#installation-rapide)
2. [Intégration dans le Dashboard](#intégration-dans-le-dashboard)
3. [Personnalisation](#personnalisation)
4. [Troubleshooting](#troubleshooting)

## Installation Rapide

### 1. Import du Composant

```typescript
import { AdvancedDonutChartComponent } from '@shared/components/advanced-donut-chart.component';
// ou
import { AdvancedDonutChartComponent } from '@shared/components';
```

### 2. Utilisation dans le Template

```html
<app-advanced-donut-chart
  [segments]="chartData"
  title="Titre du graphique"
  subtitle="Sous-titre optionnel"
></app-advanced-donut-chart>
```

### 3. Préparation des Données

```typescript
export class MyComponent {
  chartData = [
    {
      label: 'Segment 1',
      value: 30,
      color: '#dc2626',
      glowColor: '#f87171'
    },
    {
      label: 'Segment 2',
      value: 50,
      color: '#2563eb',
      glowColor: '#60a5fa'
    }
  ];
}
```

---

## Intégration dans le Dashboard

### Dashboard Administrateur

Le composant est déjà intégré dans `AdminDashboardComponent` :

```html
<!-- src/app/plate/admin/dashboard/admin-dashboard.component.html -->

<section class="charts-grid premium-section">
  <app-advanced-donut-chart
    [segments]="prioritySeries.map(s => ({
      label: s.label,
      value: s.value,
      color: s.color,
      glowColor: s.color
    }))"
    title="Priorités des Incidents"
    subtitle="Distribution temps réel avec analyse avancée"
  ></app-advanced-donut-chart>
</section>
```

**Points clés :**
- `prioritySeries` est généré depuis les données du service
- Les couleurs sont mappées automatiquement
- Le composant est standalone (pas besoin de module)

---

## Personnalisation

### 1. Utiliser un Thème Prédéfini

```typescript
import { DONUT_THEMES } from '@shared/components';

export class MyComponent {
  theme = DONUT_THEMES.neonPurple;
  
  chartData = this.theme.segments;
}
```

**Thèmes disponibles :**
- `neonPurple` (défaut)
- `cyberpunk`
- `plasma`
- `ocean`
- `forest`
- `sunset`
- `monochrome`

### 2. Créer un Thème Personnalisé

```typescript
import { DonutChartTheme } from '@shared/components';

const customTheme: DonutChartTheme = {
  name: 'My Custom Theme',
  colors: {
    gradient: {
      from: '#your-color-1',
      to: '#your-color-2'
    },
    primary: '#primary-color',
    secondary: '#secondary-color',
    accent: '#accent-color'
  },
  segments: [
    { label: 'Segment 1', color: '#color1', glowColor: '#glow1' },
    { label: 'Segment 2', color: '#color2', glowColor: '#glow2' }
  ]
};
```

### 3. Variables CSS Personnalisées

```css
/* Dans votre composant parent */
.my-chart-container {
  --primary-purple: #your-color;
  --primary-blue: #your-color;
  --primary-pink: #your-color;
  --dark-bg: #your-bg;
  --glass-bg: rgba(your, colors, here);
}
```

### 4. Modifier les Animations

Pour désactiver les animations (par ex, pour prefers-reduced-motion) :

```css
@media (prefers-reduced-motion: reduce) {
  app-advanced-donut-chart {
    /* Les animations se désactivent automatiquement */
  }
}
```

---

## Cas d'Usage Avancé

### Mise à Jour Dynamique avec RxJS

```typescript
import { Component, OnInit, OnDestroy } from '@angular/core';
import { AdvancedDonutChartComponent } from '@shared/components';
import { Subject, interval } from 'rxjs';
import { switchMap, takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-live-dashboard',
  standalone: true,
  imports: [AdvancedDonutChartComponent],
  template: `
    <app-advanced-donut-chart
      [segments]="segments$ | async"
      title="Live Analytics"
    ></app-advanced-donut-chart>
  `
})
export class LiveDashboardComponent implements OnInit, OnDestroy {
  segments$ = this.statsService.getStats().pipe(
    switchMap(stats => {
      // Mapper les stats en segments
      return of(this.mapStatsToSegments(stats));
    })
  );

  private destroy$ = new Subject<void>();

  constructor(private statsService: StatsService) {}

  ngOnInit(): void {
    // Update toutes les 5 secondes
    interval(5000)
      .pipe(
        switchMap(() => this.statsService.getStats()),
        takeUntil(this.destroy$)
      )
      .subscribe(stats => {
        // Mise à jour automatique du graphique
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private mapStatsToSegments(stats: any) {
    return [
      {
        label: 'Critique',
        value: stats.critical,
        color: '#dc2626',
        glowColor: '#f87171'
      },
      // ...
    ];
  }
}
```

### Intégration avec État Global (NgRx)

```typescript
import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { selectDashboardStats } from './dashboard.selectors';

@Component({
  selector: 'app-ngrx-dashboard',
  standalone: true,
  imports: [AdvancedDonutChartComponent, AsyncPipe],
  template: `
    <app-advanced-donut-chart
      [segments]="(stats$ | async)?.segments"
      [title]="(stats$ | async)?.title"
    ></app-advanced-donut-chart>
  `
})
export class NgRxDashboardComponent {
  stats$ = this.store.select(selectDashboardStats);

  constructor(private store: Store) {}
}
```

---

## Troubleshooting

### Problem: Graphique ne s'affiche pas

**Solution:**
1. Vérifiez que les segments ne sont pas vides
```typescript
if (!this.segments || this.segments.length === 0) {
  console.warn('No segments provided to donut chart');
}
```

2. Vérifiez les couleurs hex
```typescript
import { isValidHexColor } from '@shared/components';

segments.forEach(seg => {
  if (!isValidHexColor(seg.color)) {
    console.error(`Invalid color: ${seg.color}`);
  }
});
```

### Problem: Animations lag/stutter

**Solution:**
1. Réduisez la complexité des données
2. Vérifiez les performances du navigateur
3. Désactivez les particules si nécessaire

```css
.particles-container {
  display: none;
}
```

### Problem: Glow effect invisible

**Solution:**
1. Assurez-vous que le thème a des `glowColor` valides
2. Testez dans un environnement sans filtre CSS
3. Vérifiez la valeur d'opacité

```css
.donut-segment {
  opacity: 1; /* Important */
}
```

### Problem: Mobile responsiveness

**Solution:**
Le composant est responsif par défaut. Si problème :

```css
@media (max-width: 768px) {
  app-advanced-donut-chart {
    max-width: 100%;
    padding: 1rem;
  }
}
```

---

## Performance Tips

### 1. Optimisation des Données
```typescript
// ❌ Mauvais
segments = this.getRandomSegments(); // Nouvelle ref à chaque

// ✅ Bon
segments: DonutSegment[] = [/*...*/]; // Reference stable
```

### 2. Change Detection OnPush
```typescript
import { ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'app-my-chart',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MyChartComponent {}
```

### 3. Lazy Loading
```typescript
// Dans votre routing
{
  path: 'dashboard',
  loadComponent: () => import('./dashboard.component')
    .then(m => m.DashboardComponent)
}
```

---

## Accessibilité

Le composant respecte les standards WCAG:

- ✅ Contraste WCAG AA
- ✅ Support dark mode
- ✅ Respect des préférences `prefers-reduced-motion`
- ✅ Structure sémantique

### Tests d'Accessibilité
```bash
# Utilisez axe-core ou lighthouse
npm install -D @axe-core/react
```

---

## Déploiement

### Production Build
```bash
ng build --configuration production
```

Le composant est optimisé pour :
- Bundle size minimal
- Tree-shaking compatible
- Code splitting compatible
- Service Worker compatible

### SSR (Server-Side Rendering)
Le composant est compatible SSR. Aucune manipulation du DOM requise au serveur.

---

## Support et Contribution

Pour toute question ou amélioration :
1. Consultez la documentation complète dans `ADVANCED_DONUT_CHART_README.md`
2. Regardez les exemples dans `donut-showcase.component.ts`
3. Vérifiez les tests dans `advanced-donut-chart.component.spec.ts`

---

**Happy Charting! 🎉**

*Dernière mise à jour: Avril 2026*

