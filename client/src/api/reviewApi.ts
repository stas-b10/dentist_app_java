import { api } from './api';

import type { Notification } from '../types';

export const notificationApi = {

  // =========================
  // GET ALL NOTIFICATIONS
  // =========================

  getAll() {
    return api.get<Notification[]>(
      '/notifications'
    );
  },


  // =========================
  // GET UNREAD NOTIFICATIONS
  // =========================

  getUnread() {
    return api.get<Notification[]>(
      '/notifications/unread'
    );
  },


  // =========================
  // MARK NOTIFICATION AS READ
  // =========================

  markAsRead(id: string) {
    return api.put<void>(
      `/notifications/${id}/read`
    );
  },

};