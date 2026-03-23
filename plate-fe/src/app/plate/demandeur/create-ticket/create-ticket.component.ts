import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

interface Salle {
  idSalle: string;
  nomSalle: string;
  etage: string;
  batiment: string;
}
 
interface Equipement {
  idEquipement: string;
  nomEquipement: string;
  type: string;
  modele: string;
  numSerie: string;
  etat: 'FONCTIONNELLE' | 'EN_PANNE' | 'EN_MAINTENNANCE';
  idSalle: string;
}
 
interface Priority {
  value: string;
  label: string;
  icon: string;
}
 
interface PanneType {
  value: string;
  label: string;
  icon: string;
}


@Component({
  selector: 'app-create-ticket',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-ticket.component.html',
  styleUrl: './create-ticket.component.css'
})
export class CreateTicketComponent implements OnInit{
etatColorLight(arg0: string) {
throw new Error('Method not implemented.');
}
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
 
  priorities: Priority[] = [
    { value: 'CRITIQUE', label: 'Critique', icon: '🔴' },
    { value: 'HAUTE', label: 'Haute', icon: '🟠' },
    { value: 'NORMAL', label: 'Normal', icon: '🟡' },
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
 
  salles: Salle[] = [
    { idSalle: 'S01', nomSalle: 'Salle A01', etage: 'RDC', batiment: 'Principale' },
    { idSalle: 'S02', nomSalle: 'Salle B12', etage: '1er', batiment: 'Principale' },
    { idSalle: 'S03', nomSalle: 'Salle Labo Réseau', etage: '2ème', batiment: 'Annexe' },
    { idSalle: 'S04', nomSalle: 'Amphi 1', etage: 'RDC', batiment: 'Principale' },
    { idSalle: 'S05', nomSalle: 'Salle Informatique C3', etage: '3ème', batiment: 'Annexe' },
  ];
 
  equipements: Equipement[] = [
    { idEquipement: 'E01', nomEquipement: 'Projecteur Sony', type: 'Vidéoprojecteur', modele: 'VPL-EX435', numSerie: 'SN-2024-001', etat: 'EN_PANNE', idSalle: 'S01' },
    { idEquipement: 'E02', nomEquipement: 'PC Bureau HP', type: 'Ordinateur', modele: 'EliteDesk 800', numSerie: 'SN-2023-045', etat: 'FONCTIONNELLE', idSalle: 'S01' },
    { idEquipement: 'E03', nomEquipement: 'Tableau Interactif', type: 'TBI', modele: 'SMART Board 7086', numSerie: 'SN-2022-012', etat: 'EN_MAINTENNANCE', idSalle: 'S02' },
    { idEquipement: 'E04', nomEquipement: 'Switch Cisco', type: 'Réseau', modele: 'Catalyst 2960', numSerie: 'SN-2021-099', etat: 'FONCTIONNELLE', idSalle: 'S03' },
    { idEquipement: 'E05', nomEquipement: 'Climatiseur Daikin', type: 'Climatisation', modele: 'FTXB25C', numSerie: 'SN-2023-201', etat: 'EN_PANNE', idSalle: 'S02' },
    { idEquipement: 'E06', nomEquipement: 'Système Audio', type: 'Audio', modele: 'Bose FreeSpace', numSerie: 'SN-2020-033', etat: 'FONCTIONNELLE', idSalle: 'S04' },
  ];
 
  constructor(private fb: FormBuilder) {}
 
  ngOnInit(): void {
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
  }
 
  get f() {
    return this.ticketForm.controls;
  }
 
  get filteredEquipements(): Equipement[] {
    const idSalle = this.f['idSalle'].value;
    return idSalle ? this.equipements.filter((e) => e.idSalle === idSalle) : [];
  }
 
  get selectedEquipement(): Equipement | undefined {
    return this.equipements.find((e) => e.idEquipement === this.f['idEquipement'].value);
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
      return;
    }
    this.isLoading = true;
    const payload = {
      ...this.ticketForm.value,
      typePanne: this.selectedPanneType,
      status: 'OUVERT',
      dateCreation: new Date().toISOString(),
      photo: this.selectedFile ? this.selectedFile.name : null,
    };
    console.log('Ticket payload:', payload);
 
    setTimeout(() => {
      this.isLoading = false;
      this.submitted = true;
      this.generatedTicketId = 'TK-' + Math.floor(Math.random() * 900 + 100);
    }, 1800);
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
    if (!allowed.includes(file.type)) {
      this.fileError = 'Format non supporté. Utilisez JPG, PNG ou WEBP.';
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      this.fileError = 'Fichier trop volumineux. Maximum 5 Mo.';
      return;
    }
    this.selectedFile = file;
    const reader = new FileReader();
    reader.onload = (e) => (this.previewUrl = e.target?.result as string);
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
      NORMAL: 'text-yellow-400',
      FAIBLE: 'text-emerald-400',
    };
    return map[value] || 'text-gray-400';
  }
 
  priorityLabel(value: string): string {
    return this.priorities.find((p) => p.value === value)?.label || '';
  }
 
  salleName(id: string): string {
    return this.salles.find((s) => s.idSalle === id)?.nomSalle || '';
  }
 
  equipementName(id: string): string {
    return this.equipements.find((e) => e.idEquipement === id)?.nomEquipement || '';
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
