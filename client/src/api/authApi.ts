import { api } from './api';
import type { Session } from '../types';

export interface LoginRequest {
  email: string;
  password: string;
}

interface AuthResponse {
  token: string;
}

/*
 * This is what Spring returns from /auth/me.
 *
 * Your Java DTOs use camelCase, so the JSON should also
 * normally arrive as camelCase.
 */
interface BackendClient {
  id: number;
  userId: number;
  firstName: string | null;
  lastName: string | null;
  phone: string | null;
  dateOfBirth: string | null;
  profileImage: string | null;
}

interface BackendDentist {
  id: number;
  userId: number;
  firstName: string | null;
  lastName: string | null;
  specialization: string | null;
  phone: string | null;
  clinicName: string | null;
  experienceYears: number | null;
  biography: string | null;
  profileImage: string | null;
}

interface BackendMeResponse {
  userId: number;
  email: string;
  role: 'CLIENT' | 'DENTIST';
  client?: BackendClient | null;
  dentist?: BackendDentist | null;
}

export const authApi = {
  async login(
    data: LoginRequest
  ): Promise<Session> {

    /*
     * LOGIN
     */
    const authResponse =
      await api.post<AuthResponse>(
        '/auth/login',
        data
      );

    /*
     * Save JWT before calling /auth/me.
     */
    localStorage.setItem(
      'token',
      authResponse.token
    );

    /*
     * GET CURRENT USER
     */
    const backendSession =
      await api.get<BackendMeResponse>(
        '/auth/me'
      );

    /*
     * DEBUG
     *
     * Remove these later.
     */
    console.log(
      'AUTH /ME RESPONSE:',
      backendSession
    );

    console.log(
      'AUTH DENTIST:',
      backendSession.dentist
    );

    console.log(
      'AUTH CLIENT:',
      backendSession.client
    );

    /*
     * BUILD SESSION
     */
    const session: Session = {
      user: {
        id: String(
          backendSession.userId
        ),

        email:
          backendSession.email,

        role:
          backendSession.role,
      },

      /*
       * CLIENT
       */
      client:
        backendSession.client
          ? {
              id: String(
                backendSession.client.id
              ),

              userId: String(
                backendSession.client.userId
              ),

              firstName:
                backendSession.client.firstName ??
                '',

              lastName:
                backendSession.client.lastName ??
                '',

              phone:
                backendSession.client.phone ??
                '',

              dateOfBirth:
                backendSession.client.dateOfBirth ??
                '',

              profileImage:
                backendSession.client.profileImage ??
                null,
            }
          : undefined,

      /*
       * DENTIST
       */
      dentist:
        backendSession.dentist
          ? {
              id: String(
                backendSession.dentist.id
              ),

              userId: String(
                backendSession.dentist.userId
              ),

              firstName:
                backendSession.dentist.firstName ??
                '',

              lastName:
                backendSession.dentist.lastName ??
                '',

              specialization:
                backendSession.dentist.specialization ??
                '',

              phone:
                backendSession.dentist.phone ??
                '',

              clinicName:
                backendSession.dentist.clinicName ??
                '',

              experienceYears:
                backendSession.dentist.experienceYears ??
                0,

              biography:
                backendSession.dentist.biography ??
                '',

              profileImage:
                backendSession.dentist.profileImage ??
                null,
            }
          : undefined,
    };

    /*
     * DEBUG
     */
    console.log(
      'FINAL FRONTEND SESSION:',
      session
    );

    console.log(
      'FINAL FRONTEND DENTIST:',
      session.dentist
    );

    return session;
  },
};