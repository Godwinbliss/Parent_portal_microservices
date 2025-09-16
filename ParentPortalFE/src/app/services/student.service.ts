import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { StudentDto, ResultDto, AttendanceDto } from '../models/dtos';
import { firstValueFrom } from 'rxjs';
import { AuthService } from './auth.service';

const API_GATEWAY_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  constructor(private http: HttpClient, private authService: AuthService) { }

  private getAdminId(): number | null {
    const adminUser = this.authService.currentUser();
    if (adminUser?.role === 'ADMIN' && adminUser.id) {
      return adminUser.id;
    }
    return null;
  }

  getAllStudents(): Promise<StudentDto[]> {
    return firstValueFrom(this.http.get<StudentDto[]>(`${API_GATEWAY_URL}/api/students`));
  }

  getStudentsByParent(parentId: number): Promise<StudentDto[]> {
    return firstValueFrom(this.http.get<StudentDto[]>(`${API_GATEWAY_URL}/api/students/byParent/${parentId}`));
  }

  getStudentResults(studentId: number): Promise<ResultDto[]> {
    return firstValueFrom(this.http.get<ResultDto[]>(`${API_GATEWAY_URL}/api/students/${studentId}/results`));
  }

  getStudentAttendance(studentId: number): Promise<AttendanceDto[]> {
    return firstValueFrom(this.http.get<AttendanceDto[]>(`${API_GATEWAY_URL}/api/students/${studentId}/attendance`));
  }

  createStudent(student: any): Promise<StudentDto> {
    const adminId = this.getAdminId();
    if (!adminId) throw new Error('Action requires ADMIN role.');
    return firstValueFrom(this.http.post<StudentDto>(`${API_GATEWAY_URL}/api/students/${adminId}`, student));
  }
  
  updateStudent(id: number, student: any): Promise<StudentDto> {
    const adminId = this.getAdminId();
    if (!adminId) throw new Error('Action requires ADMIN role.');
    return firstValueFrom(this.http.put<StudentDto>(`${API_GATEWAY_URL}/api/students/${adminId}/${id}`, student));
  }

  deleteStudent(id: number): Promise<void> {
    const adminId = this.getAdminId();
    if (!adminId) throw new Error('Action requires ADMIN role.');
    return firstValueFrom(this.http.delete<void>(`${API_GATEWAY_URL}/api/students/${adminId}/${id}`));
  }

  addResultToStudent(studentId: number, result: any): Promise<ResultDto> {
    const adminId = this.getAdminId();
    if (!adminId) throw new Error('Action requires ADMIN role.');
    return firstValueFrom(this.http.post<ResultDto>(`${API_GATEWAY_URL}/api/students/${adminId}/${studentId}/results`, result));
  }
  
  deleteResult(resultId: number): Promise<void> {
    const adminId = this.getAdminId();
    if (!adminId) throw new Error('Action requires ADMIN role.');
    return firstValueFrom(this.http.delete<void>(`${API_GATEWAY_URL}/api/students/${adminId}/results/${resultId}`));
  }

  addAttendanceToStudent(studentId: number, attendance: any): Promise<AttendanceDto> {
    const adminId = this.getAdminId();
    if (!adminId) throw new Error('Action requires ADMIN role.');
    return firstValueFrom(this.http.post<AttendanceDto>(`${API_GATEWAY_URL}/api/students/${adminId}/${studentId}/attendance`, attendance));
  }
  
  deleteAttendance(attendanceId: number): Promise<void> {
    const adminId = this.getAdminId();
    if (!adminId) throw new Error('Action requires ADMIN role.');
    return firstValueFrom(this.http.delete<void>(`${API_GATEWAY_URL}/api/students/${adminId}/attendance/${attendanceId}`));
  }
}