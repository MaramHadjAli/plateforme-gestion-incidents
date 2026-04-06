/**
 * Advanced Donut Chart - Theme Configuration
 * Définit les palettes de couleurs et configurations prédéfinies
 */

export interface DonutChartTheme {
  name: string;
  colors: {
    gradient: {
      from: string;
      to: string;
    };
    primary: string;
    secondary: string;
    accent: string;
  };
  segments: {
    label: string;
    color: string;
    glowColor: string;
  }[];
}

export const DONUT_THEMES = {
  // Thème Neon Purple (Défaut)
  neonPurple: {
    name: 'Neon Purple',
    colors: {
      gradient: {
        from: '#a855f7',
        to: '#2563eb'
      },
      primary: '#a855f7',
      secondary: '#2563eb',
      accent: '#ec4899'
    },
    segments: [
      { label: 'Critical', color: '#dc2626', glowColor: '#f87171' },
      { label: 'High', color: '#f97316', glowColor: '#fb923c' },
      { label: 'Medium', color: '#eab308', glowColor: '#facc15' },
      { label: 'Low', color: '#2563eb', glowColor: '#60a5fa' }
    ]
  },

  // Thème Cyberpunk
  cyberpunk: {
    name: 'Cyberpunk',
    colors: {
      gradient: {
        from: '#ff006e',
        to: '#00f5ff'
      },
      primary: '#ff006e',
      secondary: '#00f5ff',
      accent: '#ffbe0b'
    },
    segments: [
      { label: 'Critical', color: '#ff006e', glowColor: '#ff6a9b' },
      { label: 'High', color: '#ffbe0b', glowColor: '#ffd60a' },
      { label: 'Medium', color: '#00f5ff', glowColor: '#48dbfb' },
      { label: 'Low', color: '#3a86ff', glowColor: '#5b8dee' }
    ]
  },

  // Thème Plasma
  plasma: {
    name: 'Plasma',
    colors: {
      gradient: {
        from: '#ff0080',
        to: '#7c3aed'
      },
      primary: '#ff0080',
      secondary: '#7c3aed',
      accent: '#00ffff'
    },
    segments: [
      { label: 'Critical', color: '#ff0080', glowColor: '#ff6abf' },
      { label: 'High', color: '#7c3aed', glowColor: '#a78bfa' },
      { label: 'Medium', color: '#00ffff', glowColor: '#4dd9ff' },
      { label: 'Low', color: '#06b6d4', glowColor: '#22d3ee' }
    ]
  },

  // Thème Ocean
  ocean: {
    name: 'Ocean',
    colors: {
      gradient: {
        from: '#0369a1',
        to: '#0891b2'
      },
      primary: '#0369a1',
      secondary: '#0891b2',
      accent: '#06b6d4'
    },
    segments: [
      { label: 'Critical', color: '#dc2626', glowColor: '#f87171' },
      { label: 'High', color: '#ea580c', glowColor: '#fb923c' },
      { label: 'Medium', color: '#0891b2', glowColor: '#22d3ee' },
      { label: 'Low', color: '#0369a1', glowColor: '#38bdf8' }
    ]
  },

  // Thème Forest
  forest: {
    name: 'Forest',
    colors: {
      gradient: {
        from: '#15803d',
        to: '#059669'
      },
      primary: '#15803d',
      secondary: '#059669',
      accent: '#10b981'
    },
    segments: [
      { label: 'Critical', color: '#dc2626', glowColor: '#f87171' },
      { label: 'High', color: '#f97316', glowColor: '#fb923c' },
      { label: 'Medium', color: '#059669', glowColor: '#6ee7b7' },
      { label: 'Low', color: '#15803d', glowColor: '#86efac' }
    ]
  },

  // Thème Sunset
  sunset: {
    name: 'Sunset',
    colors: {
      gradient: {
        from: '#f97316',
        to: '#dc2626'
      },
      primary: '#f97316',
      secondary: '#dc2626',
      accent: '#fbbf24'
    },
    segments: [
      { label: 'Critical', color: '#991b1b', glowColor: '#fee2e2' },
      { label: 'High', color: '#dc2626', glowColor: '#f87171' },
      { label: 'Medium', color: '#f97316', glowColor: '#fb923c' },
      { label: 'Low', color: '#fbbf24', glowColor: '#fcd34d' }
    ]
  },

  // Thème Monochrome
  monochrome: {
    name: 'Monochrome',
    colors: {
      gradient: {
        from: '#1f2937',
        to: '#6b7280'
      },
      primary: '#374151',
      secondary: '#6b7280',
      accent: '#d1d5db'
    },
    segments: [
      { label: 'Critical', color: '#1f2937', glowColor: '#4b5563' },
      { label: 'High', color: '#374151', glowColor: '#6b7280' },
      { label: 'Medium', color: '#6b7280', glowColor: '#9ca3af' },
      { label: 'Low', color: '#9ca3af', glowColor: '#d1d5db' }
    ]
  }
};

export const PRIORITY_COLOR_MAP = {
  CRITIQUE: { color: '#dc2626', glow: '#f87171' },
  HAUTE: { color: '#f97316', glow: '#fb923c' },
  NORMALE: { color: '#eab308', glow: '#facc15' },
  FAIBLE: { color: '#2563eb', glow: '#60a5fa' }
};

export const STATUS_COLOR_MAP = {
  OUVERT: { color: '#2563eb', glow: '#60a5fa' },
  ASSIGNE: { color: '#8b5cf6', glow: '#d8b4fe' },
  EN_COURS: { color: '#f59e0b', glow: '#fbbf24' },
  RESOLU: { color: '#22c55e', glow: '#86efac' }
};

/**
 * Génère une palette de couleurs aléatoire pour des données dynamiques
 */
export function generateRandomTheme(): DonutChartTheme {
  const themes = Object.values(DONUT_THEMES);
  return themes[Math.floor(Math.random() * themes.length)];
}

/**
 * Fusionne deux thèmes
 */
export function mergeThemes(...themes: Partial<DonutChartTheme>[]): DonutChartTheme {
  return <DonutChartTheme>themes.reduce((acc, theme) => ({
    ...acc,
    ...theme
  }), DONUT_THEMES.neonPurple);
}

/**
 * Obtient une couleur basée sur une priorité
 */
export function getPriorityColor(priority: string): { color: string; glow: string } {
  return (PRIORITY_COLOR_MAP as any)[priority] || {
    color: '#6b7280',
    glow: '#d1d5db'
  };
}

/**
 * Obtient une couleur basée sur un statut
 */
export function getStatusColor(status: string): { color: string; glow: string } {
  return (STATUS_COLOR_MAP as any)[status] || {
    color: '#6b7280',
    glow: '#d1d5db'
  };
}

/**
 * Valide une couleur hex
 */
export function isValidHexColor(color: string): boolean {
  return /^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$/.test(color);
}

/**
 * Lightens/Darkens a color
 */
export function adjustColor(color: string, percent: number): string {
  const num = parseInt(color.replace('#', ''), 16);
  const amt = Math.round(2.55 * percent);
  const R = (num >> 16) + amt;
  const G = (num >> 8 & 0x00FF) + amt;
  const B = (num & 0x0000FF) + amt;

  return '#' + (
    0x1000000 +
    (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 +
    (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 +
    (B < 255 ? B < 1 ? 0 : B : 255)
  ).toString(16).slice(1);
}

