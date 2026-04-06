/**
 * Advanced Donut Chart Module - Public API
 * Export principal pour utiliser les composants et utilitaires
 */

export * from './advanced-donut-chart.component';
export * from './advanced-donut-chart.theme';
export * from './donut-showcase.component';

// Types
export interface DonutSegment {
  label: string;
  value: number;
  color: string;
  glowColor: string;
}

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

// Constants
export const CHART_ANIMATIONS = {
  ROTATE_DURATION: 8000,      // ms
  PULSE_DURATION: 4000,       // ms
  COUNTER_DURATION: 500,      // ms
  REFLECTION_DURATION: 6000   // ms
};

export const CHART_DEFAULTS = {
  MAX_SEGMENT_ANIMATION_DELAY: 0.2,
  PARALLAX_MAX_DEPTH: 10,
  GLOW_BLUR: 20,
  GLASS_BACKDROP_BLUR: 20
};

