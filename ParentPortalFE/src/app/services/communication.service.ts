import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NewsDto, ChatDto, MessageDto, NotificationDto, ChatCreateDto } from '../models/dtos';
import { firstValueFrom } from 'rxjs';

const API_GATEWAY_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})
export class CommunicationService {
  
  constructor(private http: HttpClient) { }

  getNews(): Promise<NewsDto[]> {
    return firstValueFrom(this.http.get<NewsDto[]>(`${API_GATEWAY_URL}/api/communication/news`));
  }

  postNews(news: any): Promise<NewsDto> {
    return firstValueFrom(this.http.post<NewsDto>(`${API_GATEWAY_URL}/api/communication/news`, news));
  }

  getChatsByParticipant(participantId: number): Promise<ChatDto[]> {
    return firstValueFrom(this.http.get<ChatDto[]>(`${API_GATEWAY_URL}/api/communication/chats/byParticipant/${participantId}`));
  }

  getChatMessages(chatId: string): Promise<ChatDto> {
    return firstValueFrom(this.http.get<ChatDto>(`${API_GATEWAY_URL}/api/communication/chats/${chatId}`));
  }

  addMessageToChat(chatId: string, message: any): Promise<MessageDto> {
    return firstValueFrom(this.http.post<MessageDto>(`${API_GATEWAY_URL}/api/communication/chats/${chatId}/messages`, message));
  }

  getNotificationsByRecipient(recipientId: number): Promise<NotificationDto[]> {
    return firstValueFrom(this.http.get<NotificationDto[]>(`${API_GATEWAY_URL}/api/communication/notifications/byRecipient/${recipientId}`));
  }

  
  /**
   * Creates a new chat session between two participants.
   * @param chatCreateDto DTO containing the IDs of the two participants.
   * @returns A promise that resolves to the newly created ChatDto.
   */
  createChat(chatCreateDto: ChatCreateDto): Promise<ChatDto> {
    return firstValueFrom(this.http.post<ChatDto>(`${API_GATEWAY_URL}/api/communication/chats`, chatCreateDto));
  }

}