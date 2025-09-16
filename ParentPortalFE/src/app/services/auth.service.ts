import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserDto } from '../models/dtos';
import { firstValueFrom } from 'rxjs';

const API_GATEWAY_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  currentUser = signal<UserDto | null>(null);

  constructor(private http: HttpClient) { }

  async login(email: string, password: string): Promise<UserDto> {
    const loginRequest = { email, password };
    try {
      const users: UserDto[] = await firstValueFrom(this.http.get<UserDto[]>(`${API_GATEWAY_URL}/api/users`));
      const foundUser = users.find(u => u.email === email);
      if (foundUser) {
        this.currentUser.set(foundUser);
        return foundUser;
      } else {
        throw new Error('Invalid credentials');
      }
    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    }
  }

  logout(): void {
    this.currentUser.set(null);
  }

  isLoggedIn(): boolean {
    return !!this.currentUser();
  }

  isAdmin(): boolean {
    return this.currentUser()?.role === 'ADMIN';
  }

  isParent(): boolean {
    return this.currentUser()?.role === 'PARENT';
  }
}