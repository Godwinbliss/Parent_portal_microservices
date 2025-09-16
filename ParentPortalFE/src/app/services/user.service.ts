import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserDto } from '../models/dtos';
import { firstValueFrom } from 'rxjs';

const API_GATEWAY_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private http: HttpClient) { }

  getUsers(): Promise<UserDto[]> {
    return firstValueFrom(this.http.get<UserDto[]>(`${API_GATEWAY_URL}/api/users`));
  }
  
  getUserById(id: number): Promise<UserDto> {
    return firstValueFrom(this.http.get<UserDto>(`${API_GATEWAY_URL}/api/users/${id}`));
  }

  createUser(user: any): Promise<UserDto> {
    return firstValueFrom(this.http.post<UserDto>(`${API_GATEWAY_URL}/api/users`, user));
  }

  updateUser(id: number, user: any): Promise<UserDto> {
    return firstValueFrom(this.http.put<UserDto>(`${API_GATEWAY_URL}/api/users/${id}`, user));
  }

  deleteUser(id: number): Promise<void> {
    return firstValueFrom(this.http.delete<void>(`${API_GATEWAY_URL}/api/users/${id}`));
  }
}