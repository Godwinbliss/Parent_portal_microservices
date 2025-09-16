export class UserDto {
  constructor(
    public id: number,
    public username: string,
    public email: string,
    public role: string
  ) {}
}

export class LoginRequest {
  constructor(
    public username: string,
    public password: string
  ) {}
}

export class StudentDto {
  constructor(
    public id: number,
    public firstName: string,
    public lastName: string,
    public studentId: string,
    public parentUserId: number,
    public results: ResultDto[],
    public attendanceRecords: AttendanceDto[]
  ) {}
}

export class ResultDto {
  constructor(
    public id: number,
    public subject: string,
    public grade: string,
    public score: number,
    public date: string,
    public studentId: number
  ) {}
}

export class AttendanceDto {
  constructor(
    public id: number,
    public date: string,
    public status: string,
    public reason: string,
    public studentId: number
  ) {}
}

export class PaymentDto {
  constructor(
    public id: number,
    public studentId: number,
    public parentUserId: number,
    public amount: number,
    public paymentDate: string,
    public status: string,
    public transactionId: string,
    public description: string
  ) {}
}

export class NewsDto {
  constructor(
    public id: string,
    public title: string,
    public content: string,
    public publishedDate: string,
    public authorId: number,
    public authorUsername: string,
    public category: string
  ) {}
}

export class MessageDto {
  constructor(
    public id: string,
    public senderId: number,
    public senderUsername: string,
    public content: string,
    public timestamp: string,
    public read: boolean
  ) {}
}

export class ChatDto {
  constructor(
    public id: string,
    public participant1Id: number,
    public participant2Id: number,
    public participant1Username: string,
    public participant2Username: string,
    public createdAt: string,
    public lastUpdatedAt: string,
    public messages: MessageDto[]
  ) {}
}

export class ChatCreateDto {
  constructor(
    public participant1Id: number,
    public participant2Id: number
  ) {}
}

export class NotificationDto {
  constructor(
    public id: string,
    public recipientId: number,
    public recipientUsername: string,
    public message: string,
    public sentDate: string,
    public read: boolean,
    public type: string,
    public relatedEntityId: string
  ) {}
}
