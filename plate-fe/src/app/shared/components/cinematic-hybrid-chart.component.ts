import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnInit,
  OnDestroy,
  OnChanges,
  SimpleChanges,
  ChangeDetectionStrategy,
  ViewChild,
  ElementRef,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { trigger, state, style, transition, animate } from '@angular/animations';

interface Segment {
  label: string;
  value: number;
  color: string;
  glowColor?: string;
}

interface ChartState {
  mode: 'pie' | 'donut';
  selectedSegment: Segment | null;
  hoveredSegment: Segment | null;
  zoomedIn: boolean;
  rotation: number;
  particles: Particle[];
}

interface Particle {
  x: number;
  y: number;
  vx: number;
  vy: number;
  life: number;
  color: string;
  size: number;
}

interface SliceData {
  angle: number;
  sliceAngle: number;
  startAngle: number;
  endAngle: number;
  baseX: number;
  baseY: number;
  x: number;
  y: number;
  rotationX: number;
  rotationY: number;
  scaleZ: number;
  depth: number;
}

@Component({
  selector: 'app-cinematic-hybrid-chart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cinematic-hybrid-chart.component.html',
  styleUrls: ['./cinematic-hybrid-chart.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  animations: [
    trigger('modeTransition', [
      state('pie', style({
        clipPath: 'polygon(0% 0%, 100% 0%, 100% 100%, 0% 100%)'
      })),
      state('donut', style({
        clipPath: 'polygon(0% 0%, 100% 0%, 100% 100%, 0% 100%)'
      })),
      transition('pie <=> donut', [
        animate('800ms cubic-bezier(0.43, 0.13, 0.23, 0.96)')
      ])
    ]),
    trigger('sliceAnimation', [
      state('idle', style({
        transform: 'translate3d(0, 0, 0) rotateX(0deg) rotateY(0deg)',
        opacity: 1,
        filter: 'drop-shadow(0 0 0px currentColor)'
      })),
      state('hover', style({
        transform: 'translate3d(var(--tx), var(--ty), 20px) rotateX(var(--rx)) rotateY(var(--ry))',
        opacity: 1,
        filter: 'drop-shadow(0 0 20px var(--glow))'
      })),
      state('selected', style({
        transform: 'translate3d(var(--tx), var(--ty), 30px) rotateX(var(--rx)) rotateY(var(--ry))',
        opacity: 1,
        filter: 'drop-shadow(0 0 30px var(--glow))'
      })),
      transition('* <=> *', [
        animate('400ms cubic-bezier(0.34, 1.56, 0.64, 1)')
      ])
    ]),
    trigger('zoomTransition', [
      state('normal', style({
        transform: 'scale(1) translate3d(0, 0, 0)',
        opacity: 1
      })),
      state('zoomed', style({
        transform: 'scale(1.8) translate3d(0, 0, 100px)',
        opacity: 1
      })),
      transition('normal <=> zoomed', [
        animate('600ms cubic-bezier(0.4, 0, 0.2, 1)')
      ])
    ])
  ]
})
export class CinematicHybridChartComponent implements OnInit, OnDestroy, OnChanges {
  @Input() segments: Segment[] = [];
  @Input() title: string = 'Chart Interactif';
  @Input() subtitle: string = 'Survolez ou cliquez pour explorer';
  @Input() enableSound: boolean = true;
  @Input() enableParticles: boolean = true;
  @Input() darkMode: boolean = true;

  @Output() segmentSelected = new EventEmitter<Segment>();
  @Output() segmentHovered = new EventEmitter<Segment | null>();

  @ViewChild('chartCanvas', { static: false }) chartCanvas?: ElementRef<HTMLCanvasElement>;
  @ViewChild('particleCanvas', { static: false }) particleCanvas?: ElementRef<HTMLCanvasElement>;

  state: ChartState = {
    mode: 'donut',
    selectedSegment: null,
    hoveredSegment: null,
    zoomedIn: false,
    rotation: 0,
    particles: []
  };

  slices: SliceData[] = [];
  totalValue: number = 0;
  animationFrame: number | null = null;
  lastTime: number = 0;
  audioContext: AudioContext | null = null;

  // Physics parameters
  physics = {
    friction: 0.9,
    gravity: 0.05,
    bounce: 0.7,
    rotationSpeed: 0.01,
    floatAmplitude: 10,
    floatFrequency: 0.02
  };

  constructor(
    private cdr: ChangeDetectorRef,
    private elementRef: ElementRef
  ) {}

  ngOnInit(): void {
    this.initializeChart();
    this.calculateSlices();
    this.startAnimation();
    this.initializeAudio();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['segments']) {
      this.initializeChart();
      this.calculateSlices();
    }
  }

  ngOnDestroy(): void {
    if (this.animationFrame !== null) {
      cancelAnimationFrame(this.animationFrame);
    }
  }

  private initializeChart(): void {
    this.totalValue = this.segments.reduce((sum, s) => sum + s.value, 0);

    // Appliquer les couleurs de glow
    this.segments = this.segments.map(segment => ({
      ...segment,
      glowColor: segment.glowColor || this.adjustBrightness(segment.color, 150)
    }));
  }

  private calculateSlices(): void {
    if (this.segments.length === 0) return;
    
    // Si totalValue est 0, on donne une valeur fictive égale à chaque segment pour l'affichage initial
    const effectiveTotal = this.totalValue || this.segments.length;
    
    this.slices = [];
    let currentAngle = -Math.PI / 2;
    const centerX = 0;
    const centerY = 0;

    this.segments.forEach((segment, index) => {
      const val = this.totalValue === 0 ? 1 : segment.value;
      const sliceAngle = (val / effectiveTotal) * 2 * Math.PI;
      const midAngle = currentAngle + sliceAngle / 2;
      const pushDistance = 10;

      const sliceData: SliceData = {
        angle: midAngle,
        sliceAngle: sliceAngle,
        startAngle: currentAngle,
        endAngle: currentAngle + sliceAngle,
        baseX: centerX + Math.cos(midAngle) * pushDistance,
        baseY: centerY + Math.sin(midAngle) * pushDistance,
        x: centerX + Math.cos(midAngle) * pushDistance,
        y: centerY + Math.sin(midAngle) * pushDistance,
        rotationX: (Math.random() - 0.5) * 0.2,
        rotationY: (Math.random() - 0.5) * 0.2,
        scaleZ: 0.8 + Math.random() * 0.2,
        depth: index * 5
      };

      this.slices.push(sliceData);
      currentAngle += sliceAngle;
    });
  }

  private startAnimation(): void {
    const animate = (time: number) => {
      if (this.lastTime === 0) this.lastTime = time;
      const deltaTime = (time - this.lastTime) / 16.67;
      this.lastTime = time;

      this.updatePhysics(deltaTime);
      this.updateParticles(deltaTime);
      this.drawChart();

      this.animationFrame = requestAnimationFrame(animate) as any;
    };

    this.animationFrame = requestAnimationFrame(animate) as any;
  }

  private updatePhysics(deltaTime: number): void {
    if (!this.state.hoveredSegment) {
      this.state.rotation += this.physics.rotationSpeed * deltaTime;
    }

    this.slices.forEach((slice, index) => {
      const floatY = Math.sin(this.state.rotation * 0.5 + index) * this.physics.floatAmplitude;
      const floatX = Math.cos(this.state.rotation * 0.3 + index) * this.physics.floatAmplitude;

      // Correction: Ne pas accumuler, mais assigner à partir de la base
      slice.x = slice.baseX + floatX * 0.5;
      slice.y = slice.baseY + floatY * 0.5;

      slice.rotationX += (Math.random() - 0.5) * 0.01;
      slice.rotationY += (Math.random() - 0.5) * 0.01;
      slice.rotationX *= 0.98;
      slice.rotationY *= 0.98;
    });
  }

  private updateParticles(deltaTime: number): void {
    if (!this.enableParticles) return;
    this.state.particles = this.state.particles.filter(p => {
      p.x += p.vx * deltaTime;
      p.y += p.vy * deltaTime;
      p.vy += this.physics.gravity;
      p.life -= deltaTime;
      return p.life > 0;
    });
  }

  private drawChart(): void {
    const canvas = this.chartCanvas?.nativeElement;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    ctx.fillStyle = this.darkMode ? '#0f0f1e' : '#ffffff';
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    ctx.save();
    ctx.translate(canvas.width / 2, canvas.height / 2);
    this.drawDonutChart(ctx, canvas);
    ctx.restore();

    if (this.enableParticles) {
      this.drawParticles(ctx, canvas);
    }
    this.cdr.markForCheck();
  }

  private drawDonutChart(ctx: CanvasRenderingContext2D, canvas: HTMLCanvasElement): void {
    if (this.slices.length === 0) return;
    
    const outerRadius = 130;
    const innerRadius = 65;

    this.slices.forEach((slice, index) => {
      const segment = this.segments[index];
      const isHovered = this.state.hoveredSegment === segment;
      const isSelected = this.state.selectedSegment === segment;

      const offsetX = slice.x * (isHovered ? 1.5 : 1) * (isSelected ? 2 : 1);
      const offsetY = slice.y * (isHovered ? 1.5 : 1) * (isSelected ? 2 : 1);
      const dynamicInnerRadius = isSelected ? innerRadius * 0.6 : innerRadius;

      ctx.save();
      ctx.translate(offsetX, offsetY);
      ctx.rotate(this.state.rotation * 0.1);

      if (isHovered || isSelected) {
        ctx.shadowColor = segment.glowColor || segment.color;
        ctx.shadowBlur = isSelected ? 50 : 25;
      }

      ctx.beginPath();
      ctx.arc(0, 0, outerRadius, slice.startAngle, slice.endAngle);
      ctx.lineTo(
        Math.cos(slice.endAngle) * dynamicInnerRadius,
        Math.sin(slice.endAngle) * dynamicInnerRadius
      );
      ctx.arc(0, 0, dynamicInnerRadius, slice.endAngle, slice.startAngle, true);
      ctx.closePath();

      const gradient = ctx.createRadialGradient(0, 0, dynamicInnerRadius, 0, 0, outerRadius);
      gradient.addColorStop(0, this.adjustBrightness(segment.color, 220));
      gradient.addColorStop(0.5, segment.color);
      gradient.addColorStop(1, this.adjustBrightness(segment.color, 40));

      ctx.fillStyle = gradient;
      ctx.fill();

      ctx.strokeStyle = segment.glowColor || segment.color;
      ctx.lineWidth = isSelected ? 4 : 2;
      ctx.globalAlpha = isSelected ? 1 : 0.8;
      ctx.stroke();
      ctx.globalAlpha = 1;

      if (isSelected) {
        this.drawProgressBar(ctx, slice, dynamicInnerRadius);
      }
      ctx.restore();
    });
  }

  private drawProgressBar(ctx: CanvasRenderingContext2D, slice: SliceData, radius: number): void {
    const progress = (this.state.rotation % (2 * Math.PI)) / (2 * Math.PI);
    const progressAngle = slice.startAngle + (slice.sliceAngle * progress);
    ctx.beginPath();
    ctx.arc(0, 0, radius * 0.8, slice.startAngle, progressAngle);
    ctx.strokeStyle = '#ffffff';
    ctx.lineWidth = 3;
    ctx.lineCap = 'round';
    ctx.stroke();
  }

  private drawParticles(ctx: CanvasRenderingContext2D, canvas: HTMLCanvasElement): void {
    this.state.particles.forEach(particle => {
      const alpha = particle.life / 60;
      ctx.save();
      ctx.globalAlpha = alpha;
      ctx.fillStyle = particle.color;
      ctx.beginPath();
      ctx.arc(particle.x + canvas.width/2, particle.y + canvas.height/2, particle.size, 0, Math.PI * 2);
      ctx.fill();
      ctx.restore();
    });
  }

  onCanvasClick(event: MouseEvent): void {
    const canvas = this.chartCanvas?.nativeElement;
    if (!canvas) return;
    const rect = canvas.getBoundingClientRect();
    const x = event.clientX - rect.left - canvas.width / 2;
    const y = event.clientY - rect.top - canvas.height / 2;
    const clickedSegment = this.getSegmentAtPoint(x, y);
    if (clickedSegment) {
      this.selectSegment(clickedSegment);
      this.playClickSound();
      this.emitParticles(clickedSegment);
    } else if (this.state.selectedSegment) {
      this.selectSegment(null as any);
      this.playClickSound();
    }
  }

  onCanvasMouseMove(event: MouseEvent): void {
    const canvas = this.chartCanvas?.nativeElement;
    if (!canvas) return;
    const rect = canvas.getBoundingClientRect();
    const x = event.clientX - rect.left - canvas.width / 2;
    const y = event.clientY - rect.top - canvas.height / 2;
    const hoveredSegment = this.getSegmentAtPoint(x, y);
    if (hoveredSegment !== this.state.hoveredSegment) {
      this.state.hoveredSegment = hoveredSegment;
      this.segmentHovered.emit(hoveredSegment);
    }
  }

  onCanvasMouseLeave(): void {
    this.state.hoveredSegment = null;
    this.segmentHovered.emit(null);
  }

  private getSegmentAtPoint(x: number, y: number): Segment | null {
    if (this.slices.length === 0) return null;
    const distance = Math.sqrt(x * x + y * y);
    if (distance < 50 || distance > 180) return null;

    let angle = Math.atan2(y, x);
    if (angle < -Math.PI / 2) angle += 2 * Math.PI;
    
    let currentAngle = -Math.PI / 2;
    for (const segment of this.segments) {
      const val = this.totalValue === 0 ? 1 : segment.value;
      const total = this.totalValue === 0 ? this.segments.length : this.totalValue;
      const sliceAngle = (val / total) * 2 * Math.PI;
      const start = currentAngle;
      const end = currentAngle + sliceAngle;
      if (angle >= start && angle < end) return segment;
      currentAngle += sliceAngle;
    }
    return null;
  }

  selectSegment(segment: Segment | null): void {
    if (!segment || this.state.selectedSegment === segment) {
      this.state.selectedSegment = null;
      this.state.zoomedIn = false;
    } else {
      this.state.selectedSegment = segment;
      this.state.zoomedIn = true;
    }
    this.segmentSelected.emit(segment as any);
    this.cdr.markForCheck();
  }

  toggleMode(): void {}

  private emitParticles(segment: Segment): void {
    if (!this.enableParticles) return;
    const particleCount = 12;
    for (let i = 0; i < particleCount; i++) {
      const angle = (Math.PI * 2 * i) / particleCount;
      const velocity = 2 + Math.random() * 2;
      this.state.particles.push({
        x: 0,
        y: 0,
        vx: Math.cos(angle) * velocity,
        vy: Math.sin(angle) * velocity,
        life: 60,
        color: segment.glowColor || segment.color,
        size: 2 + Math.random() * 2
      });
    }
  }

  private initializeAudio(): void {
    if (!this.enableSound) return;
    try {
      const AudioContext = window.AudioContext || (window as any).webkitAudioContext;
      this.audioContext = new AudioContext();
    } catch (e) {}
  }

  private playClickSound(): void {
    if (!this.enableSound || !this.audioContext) return;
    try {
      const now = this.audioContext.currentTime;
      const oscillator = this.audioContext.createOscillator();
      const gainNode = this.audioContext.createGain();
      oscillator.connect(gainNode);
      gainNode.connect(this.audioContext.destination);
      oscillator.frequency.setValueAtTime(800, now);
      oscillator.frequency.exponentialRampToValueAtTime(400, now + 0.1);
      gainNode.gain.setValueAtTime(0.3, now);
      gainNode.gain.exponentialRampToValueAtTime(0.01, now + 0.1);
      oscillator.start(now);
      oscillator.stop(now + 0.1);
    } catch (e) {}
  }

  private adjustBrightness(hexColor: string, percent: number): string {
    const num = parseInt(hexColor.slice(1), 16);
    const amt = Math.round(2.55 * percent);
    const R = (num >> 16) + amt;
    const G = (num >> 8 & 0x00FF) + amt;
    const B = (num & 0x0000FF) + amt;
    return '#' + (0x1000000 + (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 + (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 + (B < 255 ? B < 1 ? 0 : B : 255)).toString(16).slice(1);
  }
}
