import { Component, OnInit, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AiAssistantService } from '../../core/services/ai-assistant.service';

interface Message {
  text: string;
  type: 'user' | 'bot';
  timestamp: Date;
}

@Component({
  selector: 'app-ai-assistant-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ai-assistant-chat.component.html',
  styleUrls: ['./ai-assistant-chat.component.css']
})
export class AiAssistantChatComponent implements OnInit, AfterViewChecked {
  @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

  isOpen = false;
  userInput = '';
  isTyping = false;
  messages: Message[] = [
    {
      text: 'Bonjour ! Je suis votre assistant Gemini spécialisé en maintenance préventive. Comment puis-je vous aider aujourd\'hui ?',
      type: 'bot',
      timestamp: new Date()
    }
  ];

  constructor(private aiService: AiAssistantService) {}

  ngOnInit(): void {}

  private previousMessageCount = 0;

  ngAfterViewChecked() {
    if (this.messages.length !== this.previousMessageCount) {
      this.scrollToBottom();
      this.previousMessageCount = this.messages.length;
    }
  }

  toggleChat() {
    this.isOpen = !this.isOpen;
  }

  sendMessage() {
    if (!this.userInput.trim() || this.isTyping) return;

    const userText = this.userInput.trim();
    this.messages.push({
      text: userText,
      type: 'user',
      timestamp: new Date()
    });

    this.userInput = '';
    this.isTyping = true;

    this.aiService.chat(userText).subscribe({
      next: (response) => {
        this.messages.push({
          text: response,
          type: 'bot',
          timestamp: new Date()
        });
        this.isTyping = false;
      },
      error: (err) => {
        this.messages.push({
          text: 'Désolé, une erreur est survenue lors de la communication avec Gemini.',
          type: 'bot',
          timestamp: new Date()
        });
        this.isTyping = false;
        console.error(err);
      }
    });
  }

  private scrollToBottom(): void {
    try {
      this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
    } catch (err) {}
  }
}
