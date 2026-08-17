import { api } from './api';

export interface Conversation {
  id: number;
  clientId: number;
  dentistId: number;
}

export interface ChatMessage {
  id: number;
  conversationId: number;
  senderId: number;
  content: string;
  sentAt: string;
}

export const chatApi = {
  async getConversations(): Promise<Conversation[]> {
    return api.get<Conversation[]>('/conversations');
  },

  async createOrGetConversation(
    otherUserId: number
  ): Promise<Conversation> {
    return api.post<Conversation>('/conversations', {
      otherUserId,
    });
  },

  async getMessages(
    conversationId: number
  ): Promise<ChatMessage[]> {
    return api.get<ChatMessage[]>(
      `/chat/conversation/${conversationId}`
    );
  },

  async sendMessage(
    conversationId: number,
    content: string
  ): Promise<ChatMessage> {
    return api.post<ChatMessage>(
      `/chat/conversation/${conversationId}`,
      {
        conversationId,
        content,
      }
    );
  },
};