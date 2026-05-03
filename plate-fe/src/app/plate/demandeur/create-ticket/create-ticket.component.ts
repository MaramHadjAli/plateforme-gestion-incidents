import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Priority } from '../../../core/models/priority.model';
import { PanneType } from '../../../core/models/panne-type.model';
import { Salle } from '../../../core/models/salle.model';
import { Equipement } from '../../../core/models/equipement.model';
import { TicketResponse } from '../../../core/models/ticket-response.model';
import { TicketsService } from '../../../core/services/tickets.service';
import { TicketRequest } from '../../../core/models/ticket-request.model';

import { SalleService } from '../../../core/services/salle.service';
import { EquipementService } from '../../../core/services/equipement.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-ticket',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-ticket.component.html',
  styleUrl: './create-ticket.component.css'
})
export class CreateTicketComponent implements OnInit {

  ticketForm!: FormGroup;
  currentStep = 1;
  isLoading = false;
  submitted = false;
  isDragging = false;
  previewUrl: string | null = null;
  selectedFile: File | null = null;
  fileError = '';
  selectedPanneType = '';
  generatedTicketId = '';
  today = new Date().toISOString().split('T')[0];

  ticketsList: TicketResponse[] = [];

  priorities: Priority[] = [
    { value: 'CRITIQUE', label: 'Critique', icon: '🔴' },
    { value: 'HAUTE', label: 'Haute', icon: '🟠' },
    { value: 'NORMALE', label: 'Normale', icon: '🟡' },
    { value: 'FAIBLE', label: 'Faible', icon: '🟢' },
  ];

  panneTypes: PanneType[] = [
    { value: 'Matériel', label: 'Matériel', icon: '🔧' },
    { value: 'Logiciel', label: 'Logiciel', icon: '💻' },
    { value: 'Réseau', label: 'Réseau', icon: '📡' },
    { value: 'Électrique', label: 'Électrique', icon: '⚡' },
    { value: 'Climatisation', label: 'Climatisation', icon: '❄️' },
    { value: 'Autre', label: 'Autre', icon: '🔍' },
  ];

  salles: Salle[] = []; 
  equipements: Equipement[] = [];

  constructor(
    private fb: FormBuilder,
    private ticketsService: TicketsService,
    private salleService: SalleService,
    private equipementService: EquipementService,
    private authService: AuthService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadInitialData();
    this.ticketForm = this.fb.group({
      titre: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(120)]],
      description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]],
      priorite: ['', Validators.required],
      idSalle: ['', Validators.required],
      idEquipement: ['', Validators.required],
      dateLimiteReparation: [''],
    });

    this.ticketForm.get('idSalle')!.valueChanges.subscribe(() => {
      this.ticketForm.get('idEquipement')!.setValue('');
    });

    this.loadTickets();
  }

  loadInitialData(): void {
    this.salleService.getAll().subscribe({
      next: (data) => {
        this.salles = data;
        console.log('Salles chargées:', this.salles);
      },
      error: (err) => console.error('Erreur chargement salles:', err)
    });

    this.equipementService.getAll().subscribe({
      next: (data) => {
        this.equipements = data;
        console.log('Equipements chargés:', this.equipements);
      },
      error: (err) => console.error('Erreur chargement équipements:', err)
    });
  }

  loadTickets(): void {
    this.ticketsService.getAllTickets().subscribe({
      next: (data) => {
        this.ticketsList = data;
      },
      error: (err) => {
        console.error('Error fetching tickets', err);
      }
    });
  }

  get f() { return this.ticketForm.controls; }

  get filteredEquipements(): Equipement[] {
    // On retourne maintenant tous les équipements sans filtrage par salle
    return this.equipements;
  }

  get selectedEquipement(): Equipement | undefined {
    return this.equipements.find(e => e.idEquipement === this.f['idEquipement'].value);
  }

  setPriority(value: string): void {
    this.f['priorite'].setValue(value);
    this.f['priorite'].markAsTouched();
  }

  setPanneType(value: string): void {
    this.selectedPanneType = value;
  }

  nextStep(): void {
    if (this.currentStep === 1) {
      this.f['titre'].markAsTouched();
      this.f['description'].markAsTouched();
      this.f['priorite'].markAsTouched();
      if (this.f['titre'].invalid || this.f['description'].invalid || this.f['priorite'].invalid) return;
    }
    if (this.currentStep === 2) {
      this.f['idSalle'].markAsTouched();
      this.f['idEquipement'].markAsTouched();
      if (this.f['idSalle'].invalid || this.f['idEquipement'].invalid) return;
    }
    if (this.currentStep < 3) this.currentStep++;
  }

  prevStep(): void {
    if (this.currentStep > 1) this.currentStep--;
  }

  onSubmit(): void {
    if (this.ticketForm.invalid) {
      this.ticketForm.markAllAsTouched();
      this.toastService.showError('Veuillez remplir tous les champs obligatoires.');
      return;
    }
    
    this.isLoading = true;
    const currentUser = this.authService.getCurrentUser();

    const payload: TicketRequest = {
      titre: this.f['titre'].value,
      description: this.f['description'].value,
      priorite: this.f['priorite'].value,
      dateLimite: this.f['dateLimiteReparation'].value,
      demandeurId: currentUser?.email || '', 
      idSalle: this.f['idSalle'].value,
      idEquipement: this.f['idEquipement'].value
    };

    this.ticketsService.createTicket(payload).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.submitted = true;
        this.generatedTicketId = response.idTicket; 
        this.toastService.showSuccess('Ticket créé avec succès !');
        this.loadTickets(); 
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Error creating ticket:', err);
        this.toastService.showError('Erreur lors de la création du ticket. Veuillez réessayer.');
      }
    });
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) this.processFile(input.files[0]);
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragging = true;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragging = false;
    const file = event.dataTransfer?.files[0];
    if (file) this.processFile(file);
  }

  processFile(file: File): void {
    this.fileError = '';
    const allowed = ['image/jpeg', 'image/png', 'image/webp'];
    if (!allowed.includes(file.type)) { this.fileError = 'Format non supporté. Utilisez JPG, PNG ou WEBP.'; return; }
    if (file.size > 5 * 1024 * 1024) { this.fileError = 'Fichier trop volumineux. Maximum 5 Mo.'; return; }
    this.selectedFile = file;
    const reader = new FileReader();
    reader.onload = e => this.previewUrl = e.target?.result as string;
    reader.readAsDataURL(file);
  }

  removePhoto(event: Event): void {
    event.stopPropagation();
    this.previewUrl = null;
    this.selectedFile = null;
    this.fileError = '';
  }

  etatColor(etat: string): string {
    const map: Record<string, string> = {
      FONCTIONNELLE: 'text-emerald-400',
      EN_PANNE: 'text-red-400',
      EN_MAINTENNANCE: 'text-amber-400',
    };
    return map[etat] || 'text-gray-400';
  }

  priorityTextColor(value: string): string {
    const map: Record<string, string> = {
      CRITIQUE: 'text-red-400',
      HAUTE: 'text-orange-400',
      NORMALE: 'text-yellow-400',
      FAIBLE: 'text-emerald-400',
    };
    return map[value] || 'text-gray-400';
  }

  priorityLabel(value: string): string {
    return this.priorities.find(p => p.value === value)?.label || '';
  }

  salleName(id: string): string {
    return this.salles.find(s => s.idSalle === id)?.nomSalle || '';
  }

  equipementName(id: string): string {
    return this.equipements.find(e => e.idEquipement === id)?.nomEquipement || '';
  }

  slaLabel(priority: string): string {
    const map: Record<string, string> = {
      CRITIQUE: '⏱ Résolution sous 2h',
      HAUTE: '⏱ Résolution sous 8h',
      NORMAL: '⏱ Résolution sous 24h',
      FAIBLE: '⏱ Résolution sous 72h',
    };
    return map[priority] || '— Dépend de la priorité';
  }
}