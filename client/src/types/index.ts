export type Role =
  | 'CLIENT'
  | 'DENTIST';


export interface User {
  id: string;
  email: string;
  password?: string;
  role: Role;
}


export interface Client {
  id: string;
  dateOfBirth: string;
  firstName: string;
  lastName: string;
  phone: string;
  profileImage: string | null;
  userId: string;
}


export interface Dentist {
  id: string;
  biography: string;
  clinicName: string;
  experienceYears: number;
  firstName: string;
  lastName: string;
  phone: string;
  profileImage: string | null;
  specialization: string;
  userId: string;
}


export interface Treatment {
  id: string;
  name: string;
  description: string;
  duration_minutes: number;
  price: number;
}


export type DayOfWeek =
  | 'MONDAY'
  | 'TUESDAY'
  | 'WEDNESDAY'
  | 'THURSDAY'
  | 'FRIDAY'
  | 'SATURDAY'
  | 'SUNDAY';


export interface Schedule {
  id: string;
  dentistId: string;
  dayOfWeek: DayOfWeek;
  startTime: string;
  endTime: string;
}


export type AppointmentStatus =
  | 'PENDING'
  | 'ACCEPTED'
  | 'REJECTED'
  | 'COMPLETED';


export interface Appointment {
  id: string;
  appointmentDate: string;
  startTime: string;
  endTime: string;
  status: AppointmentStatus;
  clientId: string;
  dentistId: string;
  treatmentId: string;
}


export interface CreateAppointmentRequest {
  clientId: string;
  dentistId: string;
  treatmentId: string;
  appointmentDate: string;
  startTime: string;
  endTime: string;
}


// =========================
// MEDICAL RECORD
// =========================

export interface MedicalRecord {
  id: string;

  clientId: string;

  dentistId: string;

  appointmentId: string | null;

  diagnosis: string;

  notes: string;

  treatmentPerformed: string;

  createdAt: string;
}


// =========================
// CONVERSATION
// =========================

export interface Conversation {
  id: string;

  clientId: string;

  dentistId: string;
}


export interface ConversationMessage {
  id: string;

  content: string;

  conversationId: string;

  senderId: string;

  sentAt: string;
}


export interface Notification {
  id: string;
  createdAt: string;
  message: string;
  read: boolean;
}


export interface Review {
  id: string;

  comment: string;

  createdAt: string;

  rating: number;

  appointmentId: string;

  clientId: string;

  dentistId: string;
}


export interface Session {
  user: User;

  client?: Client;

  dentist?: Dentist;
}