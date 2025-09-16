import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PaymentDto } from '../models/dtos';
import { firstValueFrom } from 'rxjs';

const API_GATEWAY_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  constructor(private http: HttpClient) { }

  getPaymentsByParent(parentId: number): Promise<PaymentDto[]> {
    return firstValueFrom(this.http.get<PaymentDto[]>(`${API_GATEWAY_URL}/api/payments/byParent/${parentId}`));
  }

  payFees(paymentDetails: any): Promise<PaymentDto> {
    return firstValueFrom(this.http.post<PaymentDto>(`${API_GATEWAY_URL}/api/payments`, paymentDetails));
  }
}