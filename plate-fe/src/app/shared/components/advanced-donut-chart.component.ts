import { Component, Input, OnInit, ViewChild, ElementRef, HostListener, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';

interface DonutSegment {
  label: string;
  value: number;
  color: string;
  glowColor: string;
}

@Component({
  selector: 'app-advanced-donut-chart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './advanced-donut-chart.component.html',
  styleUrls: ['./advanced-donut-chart.component.css']
})
export class AdvancedDonutChartComponent implements OnInit, OnChanges {
  @Input() segments: DonutSegment[] = [];
  @Input() title: string = 'Analytics';
  @Input() subtitle: string = '';

  @ViewChild('donutContainer') donutContainer!: ElementRef;

  totalValue: number = 0;
  displayedPercentage: number = 0;
  mouseX: number = 0;
  mouseY: number = 0;
  activeSegment: DonutSegment | null = null;
  segmentAngles: { start: number; end: number; percent: number }[] = [];
  segmentAngleItems: { segment: DonutSegment; angles: { start: number; end: number; percent: number } }[] = [];

  ngOnInit(): void {
    this.calculateSegments();
    this.animatePercentageCounter();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['segments']) {
      this.calculateSegments();
      this.animatePercentageCounter();
    }
  }

  calculateSegments(): void {
    this.totalValue = this.segments.reduce((sum, seg) => sum + seg.value, 0);
    const safeTotal = this.totalValue || 1;

    let currentAngle = 0;
    this.segmentAngles = this.segments.map((segment) => {
      const percent = (segment.value / safeTotal) * 100;
      const angleSize = (percent / 100) * 360;
      const start = currentAngle;
      const end = currentAngle + angleSize;
      currentAngle = end;

      return { start, end, percent };
    });

    this.segmentAngleItems = this.segments.map((segment, index) => ({
      segment,
      angles: this.segmentAngles[index] ?? { start: 0, end: 0, percent: 0 }
    }));
  }

  animatePercentageCounter(): void {
    const safeTotal = this.totalValue || 1;
    const targetPercent = this.segments.length > 0
      ? Math.round((this.segments[0].value / safeTotal) * 100)
      : 100;

    let current = 0;
    const increment = targetPercent / 30;
    const interval = setInterval(() => {
      current += increment;
      if (current >= targetPercent) {
        this.displayedPercentage = targetPercent;
        clearInterval(interval);
      } else {
        this.displayedPercentage = Math.round(current);
      }
    }, 16);
  }

  @HostListener('mousemove', ['$event'])
  onMouseMove(event: MouseEvent): void {
    if (!this.donutContainer) return;

    const rect = this.donutContainer.nativeElement.getBoundingClientRect();
    this.mouseX = event.clientX - rect.left - rect.width / 2;
    this.mouseY = event.clientY - rect.top - rect.height / 2;
  }

  @HostListener('mouseleave')
  onMouseLeave(): void {
    this.mouseX = 0;
    this.mouseY = 0;
    this.activeSegment = null;
  }

  setActiveSegment(segment: DonutSegment | null): void {
    this.activeSegment = segment;
    if (segment) {
      const safeTotal = this.totalValue || 1;
      const percent = (segment.value / safeTotal) * 100;
      this.displayedPercentage = Math.round(percent);
    }
  }

  getShadowDepth(): string {
    const depth = Math.sqrt(this.mouseX ** 2 + this.mouseY ** 2) / 10;
    return `${depth}px ${depth * 1.5}px ${depth * 3}px rgba(139, 92, 246, 0.3)`;
  }

  getGlassBlur(): string {
    const blurAmount = Math.sqrt(this.mouseX ** 2 + this.mouseY ** 2) / 50;
    return `blur(${Math.min(blurAmount, 10)}px)`;
  }
}
