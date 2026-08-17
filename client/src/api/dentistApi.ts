import { api } from './api';

import type {
  Dentist,
  Appointment,
  Schedule,
  Client,
  Treatment,
} from '../types';

export const dentistApi = {

  // =========================================================
  // GET ALL DENTISTS
  // =========================================================

  getAll() {
    return api.get<Dentist[]>(
      '/dentists'
    );
  },


  // =========================================================
  // CURRENT DENTIST
  // =========================================================

  getMe() {
    return api.get<Dentist>(
      '/dentists/me'
    );
  },


  // =========================================================
  // GET BY ID
  // =========================================================

  getById(id: string | number) {
    return api.get<Dentist>(
      `/dentists/${id}`
    );
  },


  // =========================================================
  // APPOINTMENTS
  // =========================================================

  getAppointments(id: string | number) {
    return api.get<Appointment[]>(
      `/dentists/${id}/appointments`
    );
  },


  // =========================================================
  // PATIENTS
  // =========================================================

  getPatients(id: string | number) {
    return api.get<Client[]>(
      `/dentists/${id}/patients`
    );
  },


  // =========================================================
  // SCHEDULE
  // =========================================================

  getSchedule(id: string | number) {
    return api.get<Schedule[]>(
      `/dentists/${id}/schedule`
    );
  },


  // =========================================================
  // TREATMENTS
  // =========================================================

  getTreatments(id: string | number) {
    return api.get<Treatment[]>(
      `/dentists/${id}/treatments`
    );
  },


  // =========================================================
  // UPDATE
  // =========================================================

  update(
    id: string | number,
    data: Partial<Dentist>
  ) {
    return api.put<Dentist>(
      `/dentists/${id}`,
      data
    );
  },

};