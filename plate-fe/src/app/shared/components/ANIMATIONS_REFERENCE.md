# 🎬 Advanced Donut Chart - Animation Reference

## Overview des Animations

Le composant utilise plusieurs couches d'animations pour créer un effet premium et fluide.

---

## 1. Animation de Rotation (rotateSpin)

```css
@keyframes rotateSpin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

animation: rotateSpin 8s ease-in-out infinite;
```

**Caractéristiques:**
- **Durée:** 8 secondes par tour complet
- **Easing:** ease-in-out (accélération puis décélération)
- **Répétition:** Infinie
- **Appliqué à:** SVG principal `.donut-svg`

**Effet visuel:**
- Rotation fluide et lente du graphique
- Crée une sensation de mouvement continu
- Attire l'attention sans être agressif

---

## 2. Animation Pulse (glowPulse)

```css
@keyframes glowPulse {
  0%, 100% { 
    stroke-width: 30; 
    opacity: 0.3; 
  }
  50% { 
    stroke-width: 35; 
    opacity: 0.5; 
  }
}

animation: glowPulse 3s ease-in-out infinite;
```

**Caractéristiques:**
- **Durée:** 3 secondes
- **Easing:** ease-in-out
- **Appliqué à:** Glow layer circulaire
- **Propriétés animées:** stroke-width, opacity

**Effet visuel:**
- Pulse léger du halo de lueur
- Respiration du graphique
- Renforce l'impression de vie

---

## 3. Animation Pulse des Segments (segmentPulse)

```css
@keyframes segmentPulse {
  0%, 100% { 
    stroke-width: 20;
    filter: drop-shadow(0 0 10px rgba(168, 85, 247, 0.4));
  }
  50% { 
    stroke-width: 21;
    filter: drop-shadow(0 0 15px rgba(168, 85, 247, 0.6));
  }
}

animation: segmentPulse 4s ease-in-out infinite;
```

**Caractéristiques:**
- **Durée:** 4 secondes
- **Délai variable:** Chaque segment pulse à des moments différents
- **Propriétés:** stroke-width, drop-shadow

**Effet visuel:**
- Chaque segment pulse légèrement
- L'intensité du glow varie avec la pulse
- Crée une sensation de vitalité

---

## 4. Animation Glow au Survol (segmentGlow)

```css
@keyframes segmentGlow {
  0% {
    stroke-width: 20;
    filter: drop-shadow(0 0 10px rgba(168, 85, 247, 0.4));
  }
  50% {
    stroke-width: 26;
    filter: drop-shadow(0 0 30px rgba(236, 72, 153, 0.8));
  }
  100% {
    stroke-width: 28;
    filter: drop-shadow(0 0 25px rgba(168, 85, 247, 0.8));
  }
}

animation: segmentGlow 0.6s ease-out;
```

**Déclencheur:** Classe `.active` au survol

**Caractéristiques:**
- **Durée:** 600ms
- **Easing:** ease-out (décélération douce)
- **Non répétitive:** Une fois par interaction
- **Couleur:** Rose au départ, puis retour au purple

**Effet visuel:**
- Expansion du segment au survol
- Changement de couleur de glow
- Micro-interaction satisfaisante

---

## 5. Animation Pulse du Centre (centerPulse)

```css
@keyframes centerPulse {
  0%, 100% { 
    r: 45;
    opacity: 0.4;
  }
  50% { 
    r: 46;
    opacity: 0.5;
  }
}

animation: centerPulse 4s ease-in-out infinite;
```

**Appliqué à:** Cercle central `.center-circle`

**Caractéristiques:**
- **Durée:** 4 secondes (synchronisé avec segmentPulse)
- **Rayon:** De 45px à 46px
- **Opacité:** De 0.4 à 0.5

**Effet visuel:**
- Le centre "respire" légèrement
- Crée une cohésion visuelle avec les segments

---

## 6. Animation Border Glow (borderGlow)

```css
@keyframes borderGlow {
  0%, 100% { 
    stroke: rgba(255, 255, 255, 0.1);
    stroke-width: 1;
  }
  50% { 
    stroke: rgba(168, 85, 247, 0.3);
    stroke-width: 2;
  }
}

animation: borderGlow 3s ease-in-out infinite;
```

**Appliqué à:** Bordure du centre `.center-border`

**Caractéristiques:**
- **Durée:** 3 secondes
- **Couleur transition:** Blanc → Purple
- **Épaisseur:** 1px → 2px

---

## 7. Animation Réflexion Lumineuse (reflectionShift)

```css
@keyframes reflectionShift {
  0% { 
    transform: translate(0, 0); 
    opacity: 0.5; 
  }
  50% { 
    opacity: 0.8; 
  }
  100% { 
    transform: translate(20px, 20px); 
    opacity: 0.3; 
  }
}

animation: reflectionShift 6s ease-in-out infinite;
```

**Appliqué à:** Réflexion `.light-reflection`

**Caractéristiques:**
- **Durée:** 6 secondes
- **Trajet:** De haut-gauche à bas-droite
- **Opacité:** Augmente puis diminue
- **Effet:** Pseudo-3D

**Effet visuel:**
- La lumière semble se déplacer autour de la sphère de verre
- Crée une illusion de profondeur

---

## 8. Animation Particules Flottantes (floatParticle)

```css
@keyframes floatParticle {
  0%, 100% { 
    transform: translate(0, 0) scale(1); 
    opacity: 0; 
  }
  50% { 
    transform: translate(var(--mouse-x, 0px), var(--mouse-y, 0px)) scale(1.5); 
    opacity: 1; 
  }
}

animation: floatParticle 20s infinite ease-in-out;
animation-delay: var(--particle-delay);
```

**Appliqué à:** Particules `.particle`

**Caractéristiques:**
- **Durée:** 20 secondes (très lent)
- **Délai:** Chaque particule a un délai différent (0s, 0.2s, 0.4s, etc.)
- **Trajet:** Basé sur la position de la souris

**Effet visuel:**
- Les particules flottent discrètement autour du graphique
- Elles réagissent subtilment à la souris
- Crée une ambiance très subtile et premium

---

## 9. Animation Compteur (counterAppear)

```css
@keyframes counterAppear {
  from {
    opacity: 0;
    transform: scale(0.8);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

animation: counterAppear 1.5s ease-out;
```

**Appliqué à:** Compteur `.counter-display`

**Caractéristiques:**
- **Durée:** 1.5 secondes
- **Easing:** ease-out (décélération)
- **Transformations:** Opacity + Scale

**Effet visuel:**
- Le compteur apparaît avec un "pop" subtil
- Attire l'attention naturellement
- Pas agressif grâce au délai et easing

---

## 10. Animation Label (labelSlide)

```css
@keyframes labelSlide {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

animation: labelSlide 0.6s ease-out;
```

**Appliqué à:** Labels `.label-text`

**Caractéristiques:**
- **Durée:** 600ms
- **Entrée:** Depuis le bas avec opacité 0
- **Décélération:** ease-out

**Effet visuel:**
- Le texte glisse vers le haut en se montrant
- Crée du mouvement et de la vie
- Synchronisé avec les interactions

---

## 11. Animation Légende (legendFadeIn)

```css
@keyframes legendFadeIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

animation: legendFadeIn 0.8s ease-out forwards;
animation-delay: calc(0.1s * var(--legend-index, 0));
```

**Appliqué à:** Items légende `.legend-item`

**Caractéristiques:**
- **Durée:** 800ms
- **Entrée:** Depuis la gauche avec opacité 0
- **Délai échelonné:** 100ms entre chaque item
- **Direction:** ease-out

**Effet visuel:**
- Les items de légende apparaissent l'un après l'autre
- Crée une cascade d'animation
- Très satisfaisant visuellement

---

## 12. Animation Titre (titleFadeIn)

```css
@keyframes titleFadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

animation: titleFadeIn 0.8s ease-out 0.3s both;
```

**Appliqué à:** Titre `.title-section`

**Caractéristiques:**
- **Durée:** 800ms
- **Délai:** 300ms (après le reste du contenu)
- **Fill:** both (apply start et end states)

---

## Timeline Complète

```
Temps    Événement
0ms      ├─ SVG rotation commence
         ├─ Glow pulse commence
         ├─ Segments pulse commencent
         ├─ Center pulse commence
         ├─ Particles float commence
         └─ Border glow commence

300ms    ├─ Titre commence à apparaître
         └─ Compteur commence (pendant que le titre arrive)

600ms    ├─ Labels commencent à glisser
         └─ Glow de segment commence

800ms    └─ Légende commence à fade in (avec délai échelonné)

Continu  ├─ Rotation: tous les 8 secondes
         ├─ Pulse segments: tous les 4 secondes
         ├─ Reflection: tous les 6 secondes
         └─ Particules: tous les 20 secondes
```

---

## Synchronisation des Animations

### Pulse Synchronisée
- **segmentPulse:** 4s
- **centerPulse:** 4s (même durée)
- **glowPulse:** 3s (légèrement décalée)

Cette légère différence crée une harmonie visuelle sans monotonie.

### Easing Harmony
```
counterAppear    → ease-out (rapide)
labelSlide       → ease-out (rapide)
legendFadeIn     → ease-out (rapide)
----------
segmentPulse     → ease-in-out (lent)
rotateSpin       → ease-in-out (lent)
reflectionShift  → ease-in-out (lent)
```

Les animations rapides ont un easing plus agressif (ease-out), tandis que les animations lentes ont un easing plus doux (ease-in-out).

---

## Contrôle des Animations

### Désactiver les Animations
```css
@media (prefers-reduced-motion: reduce) {
  * {
    animation: none !important;
    transition: none !important;
  }
}
```

### Ralentir les Animations (debug)
```css
.advanced-donut-wrapper * {
  animation-duration: var(--animation-speed, 1s);
}
```

### JavaScript Control
```typescript
// Pause toutes les animations
element.style.animationPlayState = 'paused';

// Reprendre
element.style.animationPlayState = 'running';
```

---

## Performance Considerations

### GPU Acceleration
Tous les `transform` et `filter` utilisent GPU acceleration automatiquement grâce aux propriétés CSS:
- `transform` ✅ (GPU)
- `opacity` ✅ (GPU)
- `stroke-width` ⚠️ (Possible layout shift)
- `filter` ⚠️ (Peut être coûteux)

### Optimisations
1. Les animations de `transform` et `opacity` sont préférées
2. Les animations rapides sont limitées en complexité
3. Les particules utilisent `will-change` implicite avec `transform`

---

## Personnalisation des Animations

### Modifier la Durée de Rotation
```css
:host {
  /* Défaut: 8s */
  --rotate-duration: 4s;
}

.donut-svg {
  animation-duration: var(--rotate-duration);
}
```

### Modifier l'Easing Global
```css
:host {
  --easing-fast: cubic-bezier(0.34, 1.56, 0.64, 1);
  --easing-slow: ease-in-out;
}
```

---

## Troubleshooting Animations

### Animations Saccadées
**Cause:** Rendering coûteux (stroke-width changes)
**Solution:** 
```css
.donut-segment {
  will-change: filter;
  filter: drop-shadow(0 0 10px transparent);
}
```

### Particules Immobiles
**Cause:** Pas de mouvement de souris detecté
**Solution:** C'est normal - les particules font des micro-mouvements subtils

### Performances Mauvaises
**Cause:** Trop de segments ou anciennes navigateurs
**Solution:**
```typescript
// Limiter les segments
if (segments.length > 12) {
  console.warn('Consider grouping segments for better performance');
}
```

---

## References

- **MDN - CSS Animations:** https://developer.mozilla.org/en-US/docs/Web/CSS/animation
- **CSS Easing Functions:** https://easings.net/
- **GPU-Accelerated Properties:** https://www.jankfree.org/

---

**Animation Design Philosophy:**
*"Smooth, subtle, and purposeful - not flashy"*

Chaque animation a une raison d'être et contribue à l'expérience utilisateur premium.

*Dernière mise à jour: Avril 2026*

