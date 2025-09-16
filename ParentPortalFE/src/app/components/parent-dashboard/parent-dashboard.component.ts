import { Component, OnInit, signal } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe, NgForOf, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { StudentService } from '../../services/student.service';
import { PaymentService } from '../../services/payment.service';
import { CommunicationService } from '../../services/communication.service';
import { UserService } from '../../services/user.service';
import { StudentDto, ResultDto, AttendanceDto, PaymentDto, NotificationDto, ChatDto, MessageDto, UserDto } from '../../models/dtos';
import { firstValueFrom } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-parent-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIf, NgForOf, DatePipe, CurrencyPipe],
  templateUrl: './parent-dashboard.component.html',
  styleUrls: ['./parent-dashboard.component.css']
})
export class ParentDashboardComponent implements OnInit {
  // --- NEW: Tab Management ---
  activeTab = signal<'performance' | 'fees'>('performance');

  students = signal<StudentDto[]>([]);
  selectedStudentId = signal<number | null>(null);
  studentResults = signal<ResultDto[]>([]);
  studentAttendance = signal<AttendanceDto[]>([]);
  performanceError = signal<string>('');

  payments = signal<PaymentDto[]>([]);
  paymentForm = { studentId: null as number | null, amount: null as number | null, description: '' };
  paymentMessage = signal<string>('');
  paymentError = signal<string>('');

  // Chat and notification signals are removed as they are handled globally

  constructor(
    public authService: AuthService,
    private studentService: StudentService,
    private paymentService: PaymentService,
    private communicationService: CommunicationService,
    private userService: UserService,
    private http: HttpClient
  ) {}

  async ngOnInit(): Promise<void> {
    const currentUser = this.authService.currentUser();
    if (currentUser && currentUser.id) {
      await this.loadStudents(currentUser.id);
      await this.loadPayments(currentUser.id);
    }
  }

  // --- NEW: Method to change tabs ---
  selectTab(tab: 'performance' | 'fees'): void {
    this.activeTab.set(tab);
  }

  async loadStudents(parentId: number): Promise<void> {
    try {
      const studentsData = await this.studentService.getStudentsByParent(parentId);
      this.students.set(studentsData || []);
    } catch (error) {
      this.performanceError.set('Failed to load student data.');
      console.error('Error loading students:', error);
    }
  }

  async onStudentSelect(): Promise<void> {
    this.performanceError.set('');
    const studentId = this.selectedStudentId();
    if (studentId) {
      try {
        const results = await this.studentService.getStudentResults(studentId);
        this.studentResults.set(results || []);
        const attendance = await this.studentService.getStudentAttendance(studentId);
        this.studentAttendance.set(attendance || []);
      } catch (error) {
        this.performanceError.set('Failed to load performance data for selected student.');
        console.error('Error loading student performance:', error);
      }
    } else {
      this.studentResults.set([]);
      this.studentAttendance.set([]);
    }
  }

  async loadPayments(parentId: number): Promise<void> {
    try {
      const paymentsData = await this.paymentService.getPaymentsByParent(parentId);
      this.payments.set(paymentsData || []);
    } catch (error) {
      this.paymentError.set('Failed to load payment history.');
      console.error('Error loading payments:', error);
    }
  }

  async onPayFees(): Promise<void> {
    this.paymentMessage.set('');
    this.paymentError.set('');
    if (!this.paymentForm.studentId || !this.paymentForm.amount || !this.paymentForm.description) {
      this.paymentError.set('Please fill all payment fields.');
      return;
    }
    try {
      const payment = {
        ...this.paymentForm,
        parentUserId: this.authService.currentUser()!.id
      };
      await this.paymentService.payFees(payment);
      this.paymentMessage.set('Payment initiated successfully!');
      this.paymentForm = { studentId: null, amount: null, description: '' };
      await this.loadPayments(this.authService.currentUser()!.id);
    } catch (error) {
      this.paymentError.set('Failed to process payment. Please try again.');
      console.error('Error paying fees:', error);
    }
  }

  getStudentName(studentId: number): string {
    const student = this.students().find(s => s.id === studentId);
    return student ? `${student.firstName} ${student.lastName}` : 'Unknown Student';
  }
}
