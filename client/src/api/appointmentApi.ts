import { api } from './api';

import type {
  Appointment,
  CreateAppointmentRequest,
} from '../types';

export const appointmentApi = {

  // =========================================================
  // GET BY ID
  // =========================================================

  getById(id: string) {
    return api.get<Appointment>(
      `/appointments/${id}`
    );
  },


  // =========================================================
  // CREATE
  // =========================================================

  create(data: CreateAppointmentRequest) {
    return api.post<Appointment>(
      '/appointments',
      data
    );
  },


  // =========================================================
  // GET ALL
  // =========================================================

  getAll() {
    return api.get<Appointment[]>(
      '/appointments'
    );
  },


  // =========================================================
  // MY APPOINTMENTS
  // =========================================================

  getMine() {
    return api.get<Appointment[]>(
      '/appointments/my'
    );
  },


  // =========================================================
  // CANCEL
  // =========================================================

  cancel(id: string) {
    return api.put<Appointment>(
      `/appointments/client/${id}/cancel`,
      {}
    );
  },


  // =========================================================
  // DENTIST ACCEPT
  // =========================================================

  accept(id: string) {
    return api.put<Appointment>(
      `/appointments/dentist/${id}/accept`,
      {}
    );
  },


  // =========================================================
  // DENTIST REJECT
  // =========================================================

  reject(id: string) {
    return api.put<Appointment>(
      `/appointments/dentist/${id}/reject`,
      {}
    );
  },


  // =========================================================
  // DENTIST COMPLETE
  // =========================================================

  complete(id: string) {
    return api.put<Appointment>(
      `/appointments/dentist/${id}/complete`,
      {}
    );
  },


  // =========================================================
  // DENTIST PENDING
  // =========================================================

  getDentistPendingRequests() {
    return api.get<Appointment[]>(
      '/appointments/dentist/pending'
    );
  },


  // =========================================================
  // AVAILABLE TIME SLOTS
  // =========================================================

  getAvailableSlots(
    dentistId: string,
    date: string,
    treatmentId: string
  ) {

    return api.get<string[]>(
      `/appointments/available?dentistId=${encodeURIComponent(
        dentistId
      )}&date=${encodeURIComponent(
        date
      )}&treatmentId=${encodeURIComponent(
        treatmentId
      )}`
    );

  },

};