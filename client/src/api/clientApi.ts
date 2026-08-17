import { api } from './api';

import type {
  Client,
  Appointment,
} from '../types';

export const clientApi = {

  // =========================
  // GET CLIENT
  // =========================

  getById(id: string) {
    return api.get<Client>(
      `/clients/${id}`
    );
  },


  // =========================
  // GET CURRENT CLIENT APPOINTMENTS
  // =========================

  getAppointments() {
    return api.get<Appointment[]>(
      '/appointments/my'
    );
  },


  // =========================
  // GET CLIENT APPOINTMENTS BY ID
  // =========================

  getAppointmentsByClientId(id: string) {
    return api.get<Appointment[]>(
      `/clients/${id}/appointments`
    );
  },

};