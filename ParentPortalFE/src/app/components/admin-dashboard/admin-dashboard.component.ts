import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule, NgForOf, NgIf, NgSwitch, NgSwitchCase, NgSwitchDefault } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { StudentService } from '../../services/student.service';
import { CommunicationService } from '../../services/communication.service';
import { UserDto, StudentDto, ResultDto, AttendanceDto, ChatDto } from '../../models/dtos';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIf, NgForOf, NgSwitch, NgSwitchCase, NgSwitchDefault],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit {
  // Navigation State
  activeTab = signal<'users' | 'students' | 'news' | 'records'>('users');

  // User Management
  users = signal<UserDto[]>([]);
  newUser = { username: '', password: '', email: '', role: '' };
  editingUser = signal<UserDto | null>(null);
  userMessage = signal<string>('');
  userError = signal<string>('');

  // News Management
  newsForm = { title: '', content: '', category: '' };
  newsMessage = signal<string>('');
  newsError = signal<string>('');

  // Student Management
  students = signal<StudentDto[]>([]);
  newStudent = { firstName: '', lastName: '', studentId: '', parentUserId: null as number | null };
  editingStudent = signal<StudentDto | null>(null);
  studentMessage = signal<string>('');
  studentError = signal<string>('');

  // Student Record Management
  newResult = { studentId: null as number | null, subject: '', grade: '', score: null as number | null, date: '' };
  newAttendance = { studentId: null as number | null, date: '', status: '', reason: '' };
  recordMessage = signal<string>('');
  recordError = signal<string>('');
  selectedStudentToRecord = signal<number | null>(null);

  // Pagination State
  pageSize = 5;
  currentPage = {
    users: signal(1),
    students: signal(1),
    records: signal(1),
    results: signal(1),  // Changed from 'records' to 'results'
    attendance: signal(1)
  };

  // Search State
  searchQuery = {
    users: signal(''),
    students: signal(''),
    records: signal(''),
    results: signal(''), // New search for results
    attendance: signal('') // New search for attendance
  };

  // Computed signals for filtered data
  filteredUsers = computed(() => {
    const query = this.searchQuery.users().toLowerCase();
    return this.users().filter(user =>
      user.username.toLowerCase().includes(query) ||
      user.email.toLowerCase().includes(query) ||
      user.role.toLowerCase().includes(query)
    );
  });
  filteredStudents = computed(() => {
    const query = this.searchQuery.students().toLowerCase();
    return this.students().filter(student =>
      student.firstName.toLowerCase().includes(query) ||
      student.lastName.toLowerCase().includes(query) ||
      student.studentId.toLowerCase().includes(query) ||
      this.getStudentName(student.parentUserId).toLowerCase().includes(query)
    );
  });

    // Add filtered computed signals for results and attendance
  filteredResults = computed(() => {
    const query = this.searchQuery.results().toLowerCase();
    return this.allResults().filter(result =>
      this.getStudentName(result.studentId).toLowerCase().includes(query) ||
      result.subject.toLowerCase().includes(query) ||
      result.grade.toLowerCase().includes(query)
    );
  });

  filteredAttendance = computed(() => {
    const query = this.searchQuery.attendance().toLowerCase();
    return this.allAttendance().filter(attendance =>
      this.getStudentName(attendance.studentId).toLowerCase().includes(query) ||
      attendance.status.toLowerCase().includes(query) ||
      (attendance.reason && attendance.reason.toLowerCase().includes(query))
    );
  });

  // Update pagination signals to use filtered data
  paginatedAllResults = computed(() => {
    const start = (this.currentPage.results() - 1) * this.pageSize;
    const end = start + this.pageSize;
    return this.filteredResults().slice(start, end);
  });

  paginatedAllAttendance = computed(() => {
    const start = (this.currentPage.attendance() - 1) * this.pageSize;
    const end = start + this.pageSize;
    return this.filteredAttendance().slice(start, end);
  });

  
  // Computed signals for paginated data
  paginatedUsers = computed(() => {
    const start = (this.currentPage.users() - 1) * this.pageSize;
    const end = start + this.pageSize;
    return this.filteredUsers().slice(start, end);
  });
  paginatedStudents = computed(() => {
    const start = (this.currentPage.students() - 1) * this.pageSize;
    const end = start + this.pageSize;
    return this.filteredStudents().slice(start, end);
  });
  
  // Computed signals for total pages
    totalPages = {
    users: computed(() => Math.ceil(this.filteredUsers().length / this.pageSize)),
    students: computed(() => Math.ceil(this.filteredStudents().length / this.pageSize)),
    results: computed(() => Math.ceil(this.filteredResults().length / this.pageSize)),
    attendance: computed(() => Math.ceil(this.filteredAttendance().length / this.pageSize))
  };
    // Computed signals for pageNumber
  pageNumbers = {
    users: computed(() => Array.from({length: this.totalPages.users()}, (_, i) => i + 1)),
    students: computed(() => Array.from({length: this.totalPages.students()}, (_, i) => i + 1)),
    results: computed(() => Array.from({length: this.totalPages.results()}, (_, i) => i + 1)),
    attendance: computed(() => Array.from({length: this.totalPages.attendance()}, (_, i) => i + 1))
  };

  // Computed signal to get only parent users
  parentUsers = computed(() => this.users().filter(user => user.role === 'PARENT'));

  // Computed signals for records management
  allResults = computed(() => this.students().flatMap(s => s.results || []));
  allAttendance = computed(() => this.students().flatMap(s => s.attendanceRecords || []));
  
  constructor(
    public authService: AuthService,
    private userService: UserService,
    private studentService: StudentService,
    private communicationService: CommunicationService
  ) {}

  async ngOnInit(): Promise<void> {
    await this.loadUsers();
    await this.loadStudents();
  }
  
  // --- Tab Navigation ---
  onTabSelect(tab: 'users' | 'students' | 'news' | 'records'): void {
    this.activeTab.set(tab);
    if (tab === 'students') {
        this.loadStudents();
    }
  }

  // --- Pagination Method ---
  onPageChange(table: 'users' | 'students' | 'records' | 'attendance' | 'results', page: number): void {
    this.currentPage[table].set(page);
  }

  // --- User Management Methods ---

  async loadUsers(): Promise<void> {
    try {
      const usersData = await this.userService.getUsers();
      this.users.set(usersData || []);
      this.currentPage.users.set(1);
    } catch (error) {
      this.userError.set('Failed to load users.');
      console.error('Error loading users:', error);
    }
  }

  async onAddUser(): Promise<void> {
    this.userMessage.set('');
    this.userError.set('');
    try {
      await this.userService.createUser(this.newUser);
      this.userMessage.set('User added successfully!');
      this.newUser = { username: '', password: '', email: '', role: '' };
      await this.loadUsers();
    } catch (error) {
      this.userError.set('Failed to add user. Check inputs and try again.');
      console.error('Error adding user:', error);
    }
  }

  onEditUser(user: UserDto): void {
    this.editingUser.set({ ...user });
  }

  async onUpdateUser(): Promise<void> {
    this.userMessage.set('');
    this.userError.set('');
    const userToUpdate = this.editingUser();
    if (!userToUpdate) return;

    try {
      await this.userService.updateUser(userToUpdate.id, userToUpdate);
      this.userMessage.set('User updated successfully!');
      this.editingUser.set(null);
      await this.loadUsers();
    } catch (error) {
      this.userError.set('Failed to update user. Check inputs and try again.');
      console.error('Error updating user:', error);
    }
  }

  cancelEdit(): void {
    this.editingUser.set(null);
  }

  async onDeleteUser(id: number): Promise<void> {
    if (confirm('Are you sure you want to delete this user?')) {
      this.userMessage.set('');
      this.userError.set('');
      try {
        await this.userService.deleteUser(id);
        this.userMessage.set('User deleted successfully!');
        await this.loadUsers();
      } catch (error) {
        this.userError.set('Failed to delete user.');
        console.error('Error deleting user:', error);
      }
    }
  }

  // --- News Management Methods ---

  async onPostNews(): Promise<void> {
    this.newsMessage.set('');
    this.newsError.set('');
    if (!this.newsForm.title || !this.newsForm.content || !this.newsForm.category) {
      this.newsError.set('Please fill all news fields.');
      return;
    }
    try {
      const newsData = {
        ...this.newsForm,
        authorId: this.authService.currentUser()!.id
      };
      await this.communicationService.postNews(newsData);
      this.newsMessage.set('News posted successfully!');
      this.newsForm = { title: '', content: '', category: '' };
    } catch (error) {
      this.newsError.set('Failed to post news.');
      console.error('Error posting news:', error);
    }
  }

  // --- Student Management Methods ---

  async loadStudents(): Promise<void> {
    try {
      const studentsData = await this.studentService.getAllStudents();
      this.students.set(studentsData || []);
      this.currentPage.students.set(1);
      this.currentPage.records.set(1);
    } catch (error) {
      this.studentError.set('Failed to load student data.');
      console.error('Error loading students:', error);
    }
  }

  async onAddStudent(): Promise<void> {
    this.studentMessage.set('');
    this.studentError.set('');
    if (!this.newStudent.firstName || !this.newStudent.lastName || !this.newStudent.studentId || !this.newStudent.parentUserId) {
        this.studentError.set('Please fill all student fields.');
        return;
    }
    try {
        await this.studentService.createStudent(this.newStudent);
        this.studentMessage.set('Student added successfully!');
        this.newStudent = { firstName: '', lastName: '', studentId: '', parentUserId: null };
        await this.loadStudents();
    } catch (error) {
        this.studentError.set('Failed to add student. Check inputs and try again.');
        console.error('Error adding student:', error);
    }
  }

  onEditStudent(student: StudentDto): void {
      this.editingStudent.set({ ...student });
  }

  async onUpdateStudent(): Promise<void> {
      this.studentMessage.set('');
      this.studentError.set('');
      const studentToUpdate = this.editingStudent();
      if (!studentToUpdate) return;
      try {
          await this.studentService.updateStudent(studentToUpdate.id, studentToUpdate);
          this.studentMessage.set('Student updated successfully!');
          this.editingStudent.set(null);
          await this.loadStudents();
      } catch (error) {
          this.studentError.set('Failed to update student. Check inputs and try again.');
          console.error('Error updating student:', error);
      }
  }

  cancelEditStudent(): void {
      this.editingStudent.set(null);
  }

  async onDeleteStudent(id: number): Promise<void> {
    if (confirm('Are you sure you want to delete this student? This will also delete all associated records.')) {
        this.studentMessage.set('');
        this.studentError.set('');
        try {
            await this.studentService.deleteStudent(id);
            this.studentMessage.set('Student deleted successfully!');
            await this.loadStudents();
        } catch (error) {
            this.studentError.set('Failed to delete student.');
            console.error('Error deleting student:', error);
        }
    }
  }
  
  // --- Student Record Management Methods (NEW) ---

  async onAddResult(): Promise<void> {
  this.recordMessage.set('');
  this.recordError.set('');
  
  const studentId = this.selectedStudentToRecord();
  if (!studentId) {
    this.recordError.set('Please select a student first');
    return;
  }

  if (!this.newResult.subject || !this.newResult.grade || !this.newResult.score || !this.newResult.date) {
    this.recordError.set('Please fill all result fields.');
    return;
  }

  try {
    const resultData = {
      ...this.newResult,
      studentId: studentId, // Ensure studentId is included
      date: this.formatDateForBackend(this.newResult.date)
    };

    await this.studentService.addResultToStudent(studentId, resultData);
    this.recordMessage.set('Result added successfully!');
    this.newResult = { studentId: null, subject: '', grade: '', score: null, date: '' };
    await this.loadStudents();
  } catch (error) {
    this.recordError.set('Failed to add result. Please check all fields.');
    console.error('Error adding result:', error);
  }
}

  async onDeleteResult(resultId: number): Promise<void> {
      if (confirm('Are you sure you want to delete this result?')) {
          this.recordMessage.set('');
          this.recordError.set('');
          try {
              await this.studentService.deleteResult(resultId);
              this.recordMessage.set('Result deleted successfully!');
              await this.loadStudents();
          } catch (error) {
              this.recordError.set('Failed to delete result.');
              console.error('Error deleting result:', error);
          }
      }
  }
  
  async onAddAttendance(): Promise<void> {
  this.recordMessage.set('');
  this.recordError.set('');
  
  const studentId = this.selectedStudentToRecord();
  if (!studentId) {
    this.recordError.set('Please select a student first');
    return;
  }

  if (!this.newAttendance.date || !this.newAttendance.status) {
    this.recordError.set('Please fill all required attendance fields.');
    return;
  }

  try {
    const attendanceData = {
      ...this.newAttendance,
      studentId: studentId, // Explicitly include studentId
      date: this.formatDateForBackend(this.newAttendance.date)
    };

    console.log('Submitting attendance:', attendanceData); // For debugging
    
    await this.studentService.addAttendanceToStudent(studentId, attendanceData);
    this.recordMessage.set('Attendance added successfully!');
    this.newAttendance = { studentId: null, date: '', status: '', reason: '' };
    await this.loadStudents();
  } catch (error) {
    const errorMessage = this.getErrorMessage(error);
    this.recordError.set(`Failed to add attendance: ${errorMessage}`);
    console.error('Error adding attendance:', error);
  }
}
  
  async onDeleteAttendance(attendanceId: number): Promise<void> {
      if (confirm('Are you sure you want to delete this attendance record?')) {
          this.recordMessage.set('');
          this.recordError.set('');
          try {
              await this.studentService.deleteAttendance(attendanceId);
              this.recordMessage.set('Attendance deleted successfully!');
              await this.loadStudents();
          } catch (error) {
              this.recordError.set('Failed to delete attendance record.');
              console.error('Error deleting attendance:', error);
          }
      }
  }
  
  // Helper function to format the date string
  private formatDateForBackend(dateString: string): string {
  if (!dateString) return '';
  
  // Convert from YYYY-MM-DD (HTML date input) to MM-DD-YYYY
  const [year, month, day] = dateString.split('-');
  return `${month}-${day}-${year}`;
}

  getStudentName(studentId: number): string {
    const student = this.students().find(s => s.id === studentId);
    return student ? `${student.firstName} ${student.lastName}` : 'Unknown Student';
  }


  // Erro Handling

  private isErrorWithMessage(error: unknown): error is { message: string } {
  return typeof error === 'object' && error !== null && 'message' in error;
}

private getErrorMessage(error: unknown): string {
  if (this.isErrorWithMessage(error)) return error.message;
  if (error instanceof HttpErrorResponse) {
    return error.error?.message || error.message || 'HTTP request failed';
  }
  if (error instanceof Error) return error.message;
  return 'An unknown error occurred';
}
}
