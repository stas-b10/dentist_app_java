import { api } from './api';
import type { Treatment } from '../types';

export const treatmentApi = {
  getAll() {
    return api.get<Treatment[]>('/treatments');
  },

  getById(id: string) {
    return api.get<Treatment>(
      `/treatments/${id}`
    );
  },
};