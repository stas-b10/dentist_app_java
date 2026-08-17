import { api } from './api';
import type { MedicalRecord } from '../types';

export const medicalRecordApi = {

  getByClient(clientId: string) {
    return api.get<MedicalRecord[]>(
      `/medical-records/client/${clientId}`
    );
  },

  getByDentist(dentistId: string) {
    return api.get<MedicalRecord[]>(
      `/medical-records/dentist/${dentistId}`
    );
  },

  getById(id: string) {
    return api.get<MedicalRecord>(
      `/medical-records/${id}`
    );
  },

  create(data: {
    clientId: number;
    dentistId: number;
    appointmentId?: number | null;
    diagnosis: string;
    notes: string;
    treatmentPerformed: string;
  }) {
    return api.post<MedicalRecord>(
      `/medical-records`,
      data
    );
  },
};