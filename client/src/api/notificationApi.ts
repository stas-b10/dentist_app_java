import { api } from './api';
import type { Notification } from '../types';

export const notificationApi = {
  getAll() {
    return api.get<Notification[]>(
      '/notifications'
    );
  },

  markAsRead(id: string) {
    return api.patch<Notification>(
      `/notifications/${id}/read`
    );
  },

  markAllAsRead() {
    return api.patch<void>(
      '/notifications/read-all'
    );
  },

  getUnreadCount() {
    return api.get<number>(
      '/notifications/unread-count'
    );
  },
};