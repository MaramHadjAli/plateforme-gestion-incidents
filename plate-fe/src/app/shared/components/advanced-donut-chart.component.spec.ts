import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdvancedDonutChartComponent } from './advanced-donut-chart.component';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

describe('AdvancedDonutChartComponent', () => {
  let component: AdvancedDonutChartComponent;
  let fixture: ComponentFixture<AdvancedDonutChartComponent>;
  let debugElement: DebugElement;

  const mockSegments = [
    { label: 'Test 1', value: 30, color: '#dc2626', glowColor: '#f87171' },
    { label: 'Test 2', value: 50, color: '#f97316', glowColor: '#fb923c' },
    { label: 'Test 3', value: 20, color: '#eab308', glowColor: '#facc15' }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdvancedDonutChartComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(AdvancedDonutChartComponent);
    component = fixture.componentInstance;
    debugElement = fixture.debugElement;
  });

  describe('Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize with empty segments', () => {
      expect(component.segments.length).toBe(0);
    });

    it('should have default title if not provided', () => {
      fixture.detectChanges();
      const titleElement = debugElement.query(By.css('.chart-title'));
      expect(titleElement?.nativeElement.textContent).toContain('Analytics');
    });

    it('should display custom title when provided', () => {
      component.title = 'Custom Title';
      fixture.detectChanges();
      const titleElement = debugElement.query(By.css('.chart-title'));
      expect(titleElement?.nativeElement.textContent).toContain('Custom Title');
    });
  });

  describe('Segment Calculation', () => {
    beforeEach(() => {
      component.segments = mockSegments;
      component.ngOnInit();
    });

    it('should calculate total value correctly', () => {
      expect(component.totalValue).toBe(100);
    });

    it('should calculate segment angles correctly', () => {
      expect(component.segmentAngles.length).toBe(3);
      expect(component.segmentAngles[0].percent).toBeCloseTo(30, 1);
      expect(component.segmentAngles[1].percent).toBeCloseTo(50, 1);
      expect(component.segmentAngles[2].percent).toBeCloseTo(20, 1);
    });

    it('should render all segments in SVG', () => {
      fixture.detectChanges();
      const circles = debugElement.queryAll(By.css('.donut-segment'));
      expect(circles.length).toBe(mockSegments.length);
    });

    it('should render legend items for each segment', () => {
      fixture.detectChanges();
      const legendItems = debugElement.queryAll(By.css('.legend-item'));
      expect(legendItems.length).toBe(mockSegments.length);
    });
  });

  describe('Animation', () => {
    beforeEach(() => {
      component.segments = mockSegments;
      component.ngOnInit();
    });

    it('should animate percentage counter on init', (done) => {
      expect(component.displayedPercentage).toBe(0);

      // Wait for animation to complete
      setTimeout(() => {
        expect(component.displayedPercentage).toBeGreaterThan(0);
        done();
      }, 50);
    });

    it('should reach target percentage', (done) => {
      setTimeout(() => {
        expect(component.displayedPercentage).toBe(30);
        done();
      }, 1000);
    });
  });

  describe('Interaction', () => {
    beforeEach(() => {
      component.segments = mockSegments;
      component.ngOnInit();
      fixture.detectChanges();
    });

    it('should set active segment on mouse enter', () => {
      const segment = mockSegments[0];
      component.setActiveSegment(segment);
      expect(component.activeSegment).toBe(segment);
    });

    it('should clear active segment on mouse leave', () => {
      component.setActiveSegment(null);
      expect(component.activeSegment).toBeNull();
    });

    it('should update displayed percentage on segment selection', () => {
      component.setActiveSegment(mockSegments[1]);
      expect(component.displayedPercentage).toBe(50);
    });

    it('should emit correct percentage for each segment', () => {
      mockSegments.forEach((segment, index) => {
        component.setActiveSegment(segment);
        const expectedPercent = Math.round((segment.value / 100) * 100);
        expect(component.displayedPercentage).toBe(expectedPercent);
      });
    });
  });

  describe('Mouse Movement', () => {
    beforeEach(() => {
      component.segments = mockSegments;
      fixture.detectChanges();
    });

    it('should track mouse position on move', () => {
      const event = new MouseEvent('mousemove', {
        clientX: 100,
        clientY: 100
      });

      component.onMouseMove(event);

      expect(component.mouseX).toBeDefined();
      expect(component.mouseY).toBeDefined();
    });

    it('should reset mouse position on leave', () => {
      component.onMouseLeave();
      expect(component.mouseX).toBe(0);
      expect(component.mouseY).toBe(0);
      expect(component.activeSegment).toBeNull();
    });

    it('should generate valid shadow depth string', () => {
      component.mouseX = 10;
      component.mouseY = 10;
      const shadow = component.getShadowDepth();
      expect(shadow).toMatch(/\d+px \d+\.?\d*px \d+\.?\d*px rgba/);
    });
  });

  describe('Responsive', () => {
    beforeEach(() => {
      component.segments = mockSegments;
      fixture.detectChanges();
    });

    it('should render within wrapper', () => {
      const wrapper = debugElement.query(By.css('.advanced-donut-wrapper'));
      expect(wrapper).toBeTruthy();
    });

    it('should have proper aspect ratio', () => {
      const container = debugElement.query(By.css('.donut-container'));
      expect(container).toBeTruthy();
    });

    it('should include particles container', () => {
      const particles = debugElement.query(By.css('.particles-container'));
      expect(particles).toBeTruthy();
    });
  });

  describe('Accessibility', () => {
    beforeEach(() => {
      component.segments = mockSegments;
      component.title = 'Test Chart';
      fixture.detectChanges();
    });

    it('should have semantic HTML structure', () => {
      const svg = debugElement.query(By.css('svg.donut-svg'));
      expect(svg).toBeTruthy();
    });

    it('should have legend for data visualization', () => {
      const legend = debugElement.query(By.css('.legend-container'));
      expect(legend).toBeTruthy();
    });

    it('should display labels for all segments', () => {
      const labels = debugElement.queryAll(By.css('.legend-label'));
      expect(labels.length).toBe(mockSegments.length);
    });
  });

  describe('Data Updates', () => {
    it('should recalculate on segments change', () => {
      component.segments = mockSegments;
      component.ngOnInit();

      const newSegments = [
        { label: 'New 1', value: 60, color: '#dc2626', glowColor: '#f87171' },
        { label: 'New 2', value: 40, color: '#f97316', glowColor: '#fb923c' }
      ];

      component.segments = newSegments;
      component.calculateSegments();

      expect(component.totalValue).toBe(100);
      expect(component.segmentAngles.length).toBe(2);
    });

    it('should handle empty segments array', () => {
      component.segments = [];
      component.calculateSegments();
      expect(component.totalValue).toBe(0);
    });

    it('should handle single segment', () => {
      component.segments = [
        { label: 'Single', value: 100, color: '#2563eb', glowColor: '#60a5fa' }
      ];
      component.calculateSegments();

      expect(component.totalValue).toBe(100);
      expect(component.segmentAngles[0].percent).toBe(100);
    });
  });

  describe('Color Handling', () => {
    beforeEach(() => {
      component.segments = mockSegments;
      fixture.detectChanges();
    });

    it('should apply colors to segments', () => {
      const circles = debugElement.queryAll(By.css('.donut-segment'));
      circles.forEach((circle, index) => {
        const stroke = circle.nativeElement.getAttribute('stroke');
        expect(stroke).toBe(mockSegments[index].color);
      });
    });

    it('should apply glow colors', () => {
      const circles = debugElement.queryAll(By.css('.donut-segment'));
      circles.forEach((circle, index) => {
        const cssVar = circle.nativeElement.style.getPropertyValue('--glow-color');
        expect(cssVar).toBeDefined();
      });
    });
  });

  describe('Performance', () => {
    it('should handle large dataset', () => {
      const largeSegments = Array.from({ length: 50 }, (_, i) => ({
        label: `Segment ${i}`,
        value: Math.random() * 100,
        color: `#${Math.floor(Math.random() * 16777215).toString(16)}`,
        glowColor: `#${Math.floor(Math.random() * 16777215).toString(16)}`
      }));

      component.segments = largeSegments;
      expect(() => component.ngOnInit()).not.toThrow();
    });

    it('should calculate quickly', () => {
      component.segments = mockSegments;

      const start = performance.now();
      component.calculateSegments();
      const end = performance.now();

      expect(end - start).toBeLessThan(10); // Should calculate in less than 10ms
    });
  });

  describe('Edge Cases', () => {
    it('should handle zero values', () => {
      component.segments = [
        { label: 'Zero', value: 0, color: '#dc2626', glowColor: '#f87171' }
      ];
      expect(() => component.calculateSegments()).not.toThrow();
    });

    it('should handle very large values', () => {
      component.segments = [
        { label: 'Large', value: 1000000, color: '#2563eb', glowColor: '#60a5fa' }
      ];
      component.calculateSegments();
      expect(component.totalValue).toBe(1000000);
    });

    it('should handle decimal values', () => {
      component.segments = [
        { label: 'Decimal', value: 33.333, color: '#eab308', glowColor: '#facc15' }
      ];
      component.calculateSegments();
      expect(component.totalValue).toBe(33.333);
    });
  });

  describe('CSS Classes', () => {
    beforeEach(() => {
      component.segments = mockSegments;
      fixture.detectChanges();
    });

    it('should have glow-layer class', () => {
      const glowLayer = debugElement.query(By.css('.glow-layer'));
      expect(glowLayer).toBeTruthy();
    });

    it('should have light-reflection class', () => {
      const reflection = debugElement.query(By.css('.light-reflection'));
      expect(reflection).toBeTruthy();
    });

    it('should have center-content class', () => {
      const content = debugElement.query(By.css('.center-content'));
      expect(content).toBeTruthy();
    });

    it('should have counter-display class', () => {
      const counter = debugElement.query(By.css('.counter-display'));
      expect(counter).toBeTruthy();
    });
  });
});

