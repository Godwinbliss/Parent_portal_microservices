// src/app/components/home/home.component.ts
import { Component, OnInit, signal } from '@angular/core';
import { CommonModule, NgForOf, NgIf } from '@angular/common';
import { CommunicationService } from '../../services/communication.service';
import { NewsDto } from '../../models/dtos';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, NgIf, NgForOf],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  news = signal<NewsDto[]>([]);
  newsError = signal<string>('');

  constructor(private communicationService: CommunicationService) {}

  async ngOnInit(): Promise<void> {
    await this.loadNews();
  }

  async loadNews(): Promise<void> {
    try {
      const newsData = await this.communicationService.getNews();
      this.news.set(newsData || []);
    } catch (error) {
      this.newsError.set('Failed to load news. Please check your network connection.');
      console.error('Error loading news:', error);
    }
  }
}
