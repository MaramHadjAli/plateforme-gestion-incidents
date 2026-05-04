import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
  selector: 'app-ticket-creation',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './ticket-creation.component.html',
  animations: [
    trigger('fadeSlideInOut', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-10px)' }),
        animate('300ms cubic-bezier(0.4, 0.0, 0.2, 1)', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ])
  ]
})
export class TicketCreationComponent implements OnInit {
  ticketForm!: FormGroup;
  isSubmitting = false;
  successMode = false;
  isDragOver = false;
  selectedFile: File | null = null;
  uploadedImageUrl: string | null = null;
  
  constructor(private fb: FormBuilder, private http: HttpClient) {}

  ngOnInit() {
    this.ticketForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5)]],
      description: ['', Validators.required],
      room: ['', Validators.required],
      type: ['', Validators.required],
      priority: ['NORMAL'],
      imageUrl: ['']
    });
  }

  // Drag and Drop listeners
  @HostListener('dragover', ['$event']) onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = true;
  }

  @HostListener('dragleave', ['$event']) onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = false;
  }

  @HostListener('drop', ['$event']) onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = false;
    
    if (event.dataTransfer && event.dataTransfer.files.length > 0) {
      this.handleFile(event.dataTransfer.files[0]);
    }
  }

  onFileSelected(event: any) {
    if (event.target.files && event.target.files.length > 0) {
      this.handleFile(event.target.files[0]);
    }
  }

  handleFile(file: File) {
    this.selectedFile = file;
    const formData = new FormData();
    formData.append('file', file);

    // Call Backend Upload API
    this.http.post<any>('http://localhost:8080/api/upload', formData).subscribe({
      next: (res) => {
        this.uploadedImageUrl = res.url;
        this.ticketForm.patchValue({ imageUrl: res.url });
      },
      error: (err) => {
        console.error('File upload failed', err);
        // Fallback for demo if backend is offline
        this.uploadedImageUrl = URL.createObjectURL(file); 
      }
    });
  }

  onSubmit() {
    if (this.ticketForm.invalid) return;
    this.isSubmitting = true;
    
    // Simulate final ticket submission API call
    setTimeout(() => {
      this.isSubmitting = false;
      this.successMode = true;
    }, 1500);
  }
}
