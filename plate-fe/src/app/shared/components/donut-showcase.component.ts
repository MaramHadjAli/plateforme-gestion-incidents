import { Component } from '@angular/core';
import { AdvancedDonutChartComponent } from '../components/advanced-donut-chart.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-donut-showcase',
  standalone: true,
  imports: [CommonModule, AdvancedDonutChartComponent],
  template: `
    <div class="showcase-wrapper">
      <h1>Advanced Donut Chart - Premium Dashboard Component</h1>
      <p class="showcase-description">
        Un composant de graphique donut ultra-avancé avec glassmorphism,
        animations fluides et interactions premium pour vos tableaux de bord.
      </p>

      <div class="charts-showcase">
        <!-- Example 1: Priority Distribution -->
        <section class="showcase-section">
          <h2>Distribution des priorités</h2>
          <app-advanced-donut-chart
            [segments]="prioritySegments"
            title="Priorités des Incidents"
            subtitle="Vue temps réel avec analyse détaillée"
          ></app-advanced-donut-chart>
        </section>

        <!-- Example 2: Status Distribution -->
        <section class="showcase-section">
          <h2>Distribution des statuts</h2>
          <app-advanced-donut-chart
            [segments]="statusSegments"
            title="Statuts des Tickets"
            subtitle="Flux opérationnel et gestion du pipeline"
          ></app-advanced-donut-chart>
        </section>

        <!-- Example 3: Custom Data -->
        <section class="showcase-section">
          <h2>Exemple personnalisé</h2>
          <app-advanced-donut-chart
            [segments]="customSegments"
            title="Performance Metrics"
            subtitle="KPIs en temps réel"
          ></app-advanced-donut-chart>
        </section>
      </div>

      <section class="features-grid">
        <h2>Fonctionnalités avancées</h2>
        <div class="features">
          <article class="feature-card">
            <h3>🎨 Glassmorphism Design</h3>
            <p>Design futuriste avec effets de verre flou et gradients soft neon (purple, blue, pink)</p>
          </article>
          <article class="feature-card">
            <h3>✨ Glowing Edges</h3>
            <p>Bords lumineux avec ombre douce et effet de lueur personnalisé par segment</p>
          </article>
          <article class="feature-card">
            <h3>🌊 Animations fluides</h3>
            <p>Pulse subtil sur les segments et effet flottant continu et gracieux</p>
          </article>
          <article class="feature-card">
            <h3>🔄 Rotation au chargement</h3>
            <p>Animation de rotation fluide et douce au chargement du composant</p>
          </article>
          <article class="feature-card">
            <h3>🖱️ Parallax sur souris</h3>
            <p>Effet de parallax dynamique basé sur le mouvement de la souris</p>
          </article>
          <article class="feature-card">
            <h3>🎯 Compteur animé</h3>
            <p>Compteur de pourcentage au centre avec animation de montée douce</p>
          </article>
          <article class="feature-card">
            <h3>💎 Réflexion lumineuse</h3>
            <p>Effet de réflexion de lumière comme du verre premium</p>
          </article>
          <article class="feature-card">
            <h3>🎪 Micro-interactions</h3>
            <p>Segments qui se dilatent et brillent au survol avec feedback visuel</p>
          </article>
          <article class="feature-card">
            <h3>✨ Particules flottantes</h3>
            <p>Petites particules discrètes qui flottent autour du graphique</p>
          </article>
          <article class="feature-card">
            <h3>🌙 Dark Mode</h3>
            <p>Design dark mode avec gradient flou en arrière-plan</p>
          </article>
          <article class="feature-card">
            <h3>📱 Responsive</h3>
            <p>Adaptatif et responsive sur tous les appareils et résolutions</p>
          </article>
          <article class="feature-card">
            <h3>🎭 Typographie minimale</h3>
            <p>Police moderne et minimaliste avec animations de texte fluides</p>
          </article>
        </div>
      </section>

      <section class="code-section">
        <h2>Utilisation simple</h2>
        <pre><code>{{ codeExample }}</code></pre>
      </section>
    </div>
  `,
  styles: [`
    .showcase-wrapper {
      max-width: 1400px;
      margin: 0 auto;
      padding: 2rem;
      background: linear-gradient(135deg, #0f172a 0%, #1a2e4a 100%);
      color: #ffffff;
      min-height: 100vh;
    }

    h1 {
      font-size: 2.5rem;
      margin-bottom: 0.5rem;
      background: linear-gradient(135deg, #a855f7, #2563eb, #ec4899);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .showcase-description {
      font-size: 1.1rem;
      color: rgba(255, 255, 255, 0.7);
      margin-bottom: 3rem;
    }

    .charts-showcase {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
      gap: 2rem;
      margin-bottom: 3rem;
    }

    .showcase-section {
      animation: slideUp 0.8s ease-out;
    }

    .showcase-section h2 {
      font-size: 1.3rem;
      margin-bottom: 1.5rem;
      color: #a855f7;
    }

    .features-grid h2 {
      font-size: 2rem;
      margin-bottom: 2rem;
      text-align: center;
    }

    .features {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 1.5rem;
      margin-bottom: 3rem;
    }

    .feature-card {
      padding: 1.5rem;
      border-radius: 1rem;
      background: rgba(168, 85, 247, 0.1);
      border: 1px solid rgba(168, 85, 247, 0.3);
      backdrop-filter: blur(10px);
      transition: all 0.3s ease;
    }

    .feature-card:hover {
      background: rgba(168, 85, 247, 0.2);
      border-color: rgba(168, 85, 247, 0.5);
      transform: translateY(-5px);
    }

    .feature-card h3 {
      margin: 0 0 0.5rem;
      font-size: 1.1rem;
    }

    .feature-card p {
      margin: 0;
      color: rgba(255, 255, 255, 0.7);
      font-size: 0.95rem;
    }

    .code-section {
      background: rgba(15, 23, 42, 0.8);
      border: 1px solid rgba(168, 85, 247, 0.2);
      border-radius: 1rem;
      padding: 2rem;
      margin-top: 2rem;
    }

    .code-section h2 {
      margin-top: 0;
    }

    pre {
      background: rgba(0, 0, 0, 0.3);
      padding: 1.5rem;
      border-radius: 0.8rem;
      overflow-x: auto;
      font-size: 0.9rem;
    }

    code {
      color: #a855f7;
      font-family: 'Courier New', monospace;
    }

    @keyframes slideUp {
      from {
        opacity: 0;
        transform: translateY(20px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }
  `]
})
export class DonutShowcaseComponent {
  prioritySegments = [
    { label: 'Critique', value: 45, color: '#dc2626', glowColor: '#f87171' },
    { label: 'Haute', value: 32, color: '#f97316', glowColor: '#fb923c' },
    { label: 'Moyenne', value: 78, color: '#eab308', glowColor: '#facc15' },
    { label: 'Faible', value: 91, color: '#2563eb', glowColor: '#60a5fa' }
  ];

  statusSegments = [
    { label: 'Ouvert', value: 28, color: '#2563eb', glowColor: '#60a5fa' },
    { label: 'Assigné', value: 45, color: '#8b5cf6', glowColor: '#d8b4fe' },
    { label: 'En cours', value: 102, color: '#f59e0b', glowColor: '#fbbf24' },
    { label: 'Résolu', value: 71, color: '#22c55e', glowColor: '#86efac' }
  ];

  customSegments = [
    { label: 'API Uptime', value: 99.8, color: '#22c55e', glowColor: '#86efac' },
    { label: 'Response Time', value: 98.2, color: '#2563eb', glowColor: '#60a5fa' },
    { label: 'Cache Hit', value: 95.5, color: '#a855f7', glowColor: '#d8b4fe' },
    { label: 'Error Rate', value: 4.3, color: '#ec4899', glowColor: '#f472b6' }
  ];

  codeExample = `import { Component } from '@angular/core';
import { AdvancedDonutChartComponent } from '@shared/components/advanced-donut-chart.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [AdvancedDonutChartComponent],
  template: \`
    <app-advanced-donut-chart
      [segments]="chartData"
      title="Priorités des Incidents"
      subtitle="Vue temps réel"
    ></app-advanced-donut-chart>
  \`
})
export class DashboardComponent {
  chartData = [
    {
      label: 'Critique',
      value: 45,
      color: '#dc2626',
      glowColor: '#f87171'
    },
    {
      label: 'Haute',
      value: 32,
      color: '#f97316',
      glowColor: '#fb923c'
    },
    // ... more segments
  ];
}`;
}

