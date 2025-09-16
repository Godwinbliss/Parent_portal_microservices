// login.component.ts
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { AppComponent } from '../../app.component';
import { UserDto } from '../../models/dtos';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  email = ''; // Changed from username to email
  password = '';
  errorMessage = '';

  constructor(private authService: AuthService, private appComponent: AppComponent) {}

  async onLogin(): Promise<void> {
    try {
      this.errorMessage = '';
      // Pass email 
      const user = await this.authService.login(this.email, this.password);
      if (user) {
        this.appComponent.navigateToDashboard();
      }
    } catch (error) {
      this.errorMessage = 'Invalid email or password. Please try again.'; // Update error message
    }
  }
}