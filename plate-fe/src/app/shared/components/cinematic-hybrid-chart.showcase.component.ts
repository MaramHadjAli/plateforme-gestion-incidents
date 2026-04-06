import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CinematicHybridChartComponent } from './cinematic-hybrid-chart.component';

interface Segment {
  label: string;
  value: number;
  color: string;
  glowColor?: string;
}

/**
 * Example Component demonstrating CinematicHybridChartComponent usage
 * Provides multiple dataset examples and interaction handlers
 */
@Component({
  selector: 'app-cinematic-chart-showcase',
  standalone: true,
  imports: [CommonModule, CinematicHybridChartComponent],
  template: `
    <div class="showcase-container">
      <h1>🎬 Cinematic Hybrid Chart Showcase</h1>

      <!-- Example 1: Incidents Priority -->
      <section class="showcase-section">
        <h2>Incidents by Priority</h2>
        <p class="description">Interactive visualization of incident severity distribution</p>
        <app-cinematic-hybrid-chart
          [segments]="incidentsPriority"
          [title]="'Incidents by Priority'"
          [subtitle]="'Click to zoom into category'"
          [enableSound]="true"
          [enableParticles]="true"
          [darkMode]="true"
          (segmentSelected)="handleIncidentSelect($event)"
          (segmentHovered)="handleIncidentHover($event)"
        ></app-cinematic-hybrid-chart>
        <div class="interaction-log">
          <div *ngIf="lastSelectedIncident">
            <strong>Selected:</strong> {{ lastSelectedIncident.label }} ({{ lastSelectedIncident.value }})
          </div>
          <div *ngIf="lastHoveredIncident">
            <strong>Hovering:</strong> {{ lastHoveredIncident.label }}
          </div>
        </div>
      </section>

      <!-- Example 2: System Status -->
      <section class="showcase-section">
        <h2>System Status Distribution</h2>
        <p class="description">Real-time system component health metrics</p>
        <app-cinematic-hybrid-chart
          [segments]="systemStatus"
          [title]="'System Components Status'"
          [subtitle]="'Explore component health'"
          [enableSound]="true"
          [enableParticles]="true"
          [darkMode]="true"
          (segmentSelected)="handleStatusSelect($event)"
        ></app-cinematic-hybrid-chart>
      </section>

      <!-- Example 3: User Activity -->
      <section class="showcase-section">
        <h2>User Activity Timeline</h2>
        <p class="description">Activity distribution across different user types</p>
        <app-cinematic-hybrid-chart
          [segments]="userActivity"
          [title]="'User Activity Distribution'"
          [subtitle]="'Hover to see details'"
          [enableSound]="false"
          [enableParticles]="true"
          [darkMode]="true"
        ></app-cinematic-hybrid-chart>
      </section>

      <!-- Example 4: Performance Metrics -->
      <section class="showcase-section">
        <h2>Performance Metrics</h2>
        <p class="description">API performance breakdown</p>
        <app-cinematic-hybrid-chart
          [segments]="performanceMetrics"
          [title]="'API Performance Breakdown'"
          [subtitle]="'Interactive metric explorer'"
          [enableSound]="true"
          [enableParticles]="true"
          [darkMode]="true"
          (segmentSelected)="handlePerformanceSelect($event)"
        ></app-cinematic-hybrid-chart>
      </section>

      <!-- Example 5: Light Mode -->
      <section class="showcase-section light-mode">
        <h2>Light Mode Example</h2>
        <p class="description">Cinematic chart in light theme</p>
        <app-cinematic-hybrid-chart
          [segments]="bugSeverity"
          [title]="'Bug Severity Distribution'"
          [subtitle]="'Light mode demonstration'"
          [enableSound]="true"
          [enableParticles]="false"
          [darkMode]="false"
        ></app-cinematic-hybrid-chart>
      </section>

      <!-- Stats Panel -->
      <section class="stats-panel">
        <h3>📊 Interaction Stats</h3>
        <div class="stat-row">
          <span class="stat-label">Total Selections:</span>
          <span class="stat-value">{{ selectionCount }}</span>
        </div>
        <div class="stat-row">
          <span class="stat-label">Last Hovered:</span>
          <span class="stat-value">{{ lastHoveredSegment | json }}</span>
        </div>
        <div class="stat-row">
          <span class="stat-label">Animation FPS:</span>
          <span class="stat-value">60 (GPU Accelerated)</span>
        </div>
      </section>
    </div>
  `,
  styles: [`
    .showcase-container {
      padding: 2rem;
      background: linear-gradient(135deg, #0f0f1e 0%, #1a1a2e 100%);
      color: #ffffff;
      min-height: 100vh;
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    }

    h1 {
      text-align: center;
      font-size: 2.5rem;
      margin-bottom: 3rem;
      background: linear-gradient(90deg, #00ff88, #0088ff);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .showcase-section {
      margin-bottom: 4rem;
      padding: 2rem;
      background: rgba(0, 255, 136, 0.05);
      border: 1px solid rgba(0, 255, 136, 0.1);
      border-radius: 1.5rem;
      backdrop-filter: blur(10px);
    }

    .showcase-section.light-mode {
      background: linear-gradient(135deg, #f5f5f5 0%, #ffffff 100%);
      border: 1px solid rgba(0, 0, 0, 0.1);
    }

    .showcase-section.light-mode h2 {
      color: #1a1a1a;
    }

    .showcase-section.light-mode .description {
      color: #666666;
    }

    .showcase-section h2 {
      margin-top: 0;
      margin-bottom: 0.5rem;
      font-size: 1.5rem;
    }

    .description {
      color: #a0a0b0;
      font-size: 0.9rem;
      margin-bottom: 1.5rem;
    }

    .interaction-log {
      margin-top: 1rem;
      padding: 1rem;
      background: rgba(0, 255, 136, 0.1);
      border-left: 3px solid #00ff88;
      border-radius: 0.5rem;
      font-size: 0.9rem;
    }

    .interaction-log div {
      margin: 0.5rem 0;
    }

    .interaction-log strong {
      color: #00ff88;
    }

    .stats-panel {
      max-width: 400px;
      margin: 3rem auto 0;
      padding: 1.5rem;
      background: rgba(0, 136, 255, 0.08);
      border: 1px solid rgba(0, 136, 255, 0.2);
      border-radius: 1rem;
    }

    .stats-panel h3 {
      margin-top: 0;
      text-align: center;
      color: #0088ff;
    }

    .stat-row {
      display: flex;
      justify-content: space-between;
      padding: 0.5rem 0;
      border-bottom: 1px solid rgba(0, 136, 255, 0.1);
      font-size: 0.85rem;
    }

    .stat-row:last-child {
      border-bottom: none;
    }

    .stat-label {
      color: #a0a0b0;
    }

    .stat-value {
      color: #0088ff;
      font-weight: 600;
    }

    @media (max-width: 768px) {
      .showcase-container {
        padding: 1rem;
      }

      h1 {
        font-size: 1.8rem;
        margin-bottom: 2rem;
      }

      .showcase-section {
        padding: 1rem;
        margin-bottom: 2rem;
      }
    }
  `]
})
export class CinematicChartShowcaseComponent implements OnInit {
  // Dataset Examples
  incidentsPriority: Segment[] = [];
  systemStatus: Segment[] = [];
  userActivity: Segment[] = [];
  performanceMetrics: Segment[] = [];
  bugSeverity: Segment[] = [];

  // Interaction Tracking
  selectionCount = 0;
  lastSelectedIncident: Segment | null = null;
  lastHoveredIncident: Segment | null = null;
  lastHoveredSegment: Segment | null = null;

  ngOnInit(): void {
    this.initializeDatasets();
  }

  private initializeDatasets(): void {
    // Example 1: Incidents by Priority
    this.incidentsPriority = [
      { label: '🔴 Critical', value: 45, color: '#ff0088', glowColor: '#ff4db3' },
      { label: '🟠 High', value: 32, color: '#00ff88', glowColor: '#00ffaa' },
      { label: '🟡 Medium', value: 28, color: '#0088ff', glowColor: '#00aaff' },
      { label: '🟢 Low', value: 15, color: '#ffaa00', glowColor: '#ffcc00' }
    ];

    // Example 2: System Status
    this.systemStatus = [
      { label: 'Healthy', value: 340, color: '#00ff88', glowColor: '#00ffaa' },
      { label: 'Warning', value: 85, color: '#ffaa00', glowColor: '#ffcc00' },
      { label: 'Critical', value: 23, color: '#ff0088', glowColor: '#ff4db3' },
      { label: 'Offline', value: 2, color: '#666666', glowColor: '#888888' }
    ];

    // Example 3: User Activity
    this.userActivity = [
      { label: 'Admin Users', value: 12, color: '#ff0088', glowColor: '#ff4db3' },
      { label: 'Regular Users', value: 156, color: '#00ff88', glowColor: '#00ffaa' },
      { label: 'Guest Users', value: 89, color: '#0088ff', glowColor: '#00aaff' },
      { label: 'API Clients', value: 45, color: '#ffaa00', glowColor: '#ffcc00' }
    ];

    // Example 4: Performance Metrics
    this.performanceMetrics = [
      { label: '< 100ms', value: 523, color: '#00ff88', glowColor: '#00ffaa' },
      { label: '100-500ms', value: 234, color: '#0088ff', glowColor: '#00aaff' },
      { label: '500ms-1s', value: 89, color: '#ffaa00', glowColor: '#ffcc00' },
      { label: '> 1s', value: 34, color: '#ff0088', glowColor: '#ff4db3' }
    ];

    // Example 5: Bug Severity
    this.bugSeverity = [
      { label: 'Critical', value: 8, color: '#ff0088', glowColor: '#ff4db3' },
      { label: 'Major', value: 23, color: '#ff6600', glowColor: '#ff8833' },
      { label: 'Minor', value: 67, color: '#ffcc00', glowColor: '#ffdd33' },
      { label: 'Trivial', value: 112, color: '#00cc66', glowColor: '#00ff88' }
    ];
  }

  handleIncidentSelect(segment: Segment): void {
    this.selectionCount++;
    this.lastSelectedIncident = segment;
    console.log('Incident selected:', segment);
  }

  handleIncidentHover(segment: Segment | null): void {
    this.lastHoveredIncident = segment;
  }

  handleStatusSelect(segment: Segment): void {
    this.selectionCount++;
    console.log('Status segment selected:', segment);
  }

  handlePerformanceSelect(segment: Segment): void {
    this.selectionCount++;
    console.log('Performance metric selected:', segment);
  }
}

