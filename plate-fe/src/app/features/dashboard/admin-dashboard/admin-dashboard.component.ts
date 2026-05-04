import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChartConfiguration, ChartOptions, ChartType } from 'chart.js';
import { BaseChartDirective, provideCharts, withDefaultRegisterables } from 'ng2-charts';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  providers: [provideCharts(withDefaultRegisterables())],
  templateUrl: './admin-dashboard.component.html',
})
export class AdminDashboardComponent implements OnInit {
  
  // KPI Data
  stats = {
    totalTickets: 124,
    openTickets: 32,
    criticalSLA: 5,
    avgResolution: '4.2 Hrs'
  };

  // Line Chart: Tickets Over Time
  public lineChartData: ChartConfiguration<'line'>['data'] = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
    datasets: [
      {
        data: [65, 59, 80, 81, 56, 55, 40],
        label: 'Resolved Tickets',
        fill: true,
        tension: 0.5,
        borderColor: '#4F46E5',
        backgroundColor: 'rgba(79, 70, 229, 0.2)'
      }
    ]
  };
  public lineChartOptions: ChartOptions<'line'> = { responsive: true };

  // Pie Chart: Tickets by Status
  public pieChartOptions: ChartOptions<'pie'> = { responsive: true };
  public pieChartLabels = ['Open', 'In Progress', 'Resolved', 'Closed'];
  public pieChartDatasets = [{
    data: [32, 14, 50, 28],
    backgroundColor: ['#FCD34D', '#3B82F6', '#10B981', '#9CA3AF']
  }];
  public pieChartLegend = true;

  // Bar Chart: Tech Performance
  public barChartOptions: ChartOptions<'bar'> = { responsive: true };
  public barChartLabels = ['Tech A', 'Tech B', 'Tech C', 'Tech D'];
  public barChartDatasets = [
    { data: [45, 38, 52, 29], label: 'Tickets Solved', backgroundColor: '#6366F1' }
  ];

  ngOnInit() {
    // Initiate WebSocket config or Real-time listeners here
  }
}
