

export * from './advanced-donut-chart.component';
export * from './advanced-donut-chart.theme';
export * from './donut-showcase.component';


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


export const CHART_ANIMATIONS = {
  ROTATE_DURATION: 8000,
  PULSE_DURATION: 4000,
  COUNTER_DURATION: 500,
  REFLECTION_DURATION: 6000
};

export const CHART_DEFAULTS = {
  MAX_SEGMENT_ANIMATION_DELAY: 0.2,
  PARALLAX_MAX_DEPTH: 10,
  GLOW_BLUR: 20,
  GLASS_BACKDROP_BLUR: 20
};

