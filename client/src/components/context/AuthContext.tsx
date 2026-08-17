import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from 'react';

import type { Session } from '../../types';
import { authApi } from '../../api/authApi';

interface AuthContextValue {
  session: Session | null;
  loading: boolean;
  login: (
    email: string,
    password: string
  ) => Promise<void>;
  logout: () => void;
}

const AuthContext =
  createContext<AuthContextValue | undefined>(
    undefined
  );

const SESSION_KEY = 'dentist_session';

export function AuthProvider({
  children,
}: {
  children: ReactNode;
}) {
  const [session, setSession] =
    useState<Session | null>(null);

  const [loading, setLoading] =
    useState(true);

  /*
   * Load saved session.
   */
  useEffect(() => {
    const saved =
      localStorage.getItem(SESSION_KEY);

    if (!saved) {
      setLoading(false);
      return;
    }

    try {
      const parsed: Session =
        JSON.parse(saved);

      /*
       * Validate the saved session.
       *
       * This prevents old sessions with
       * first_name / last_name from being used.
       */
      if (
        !parsed.user ||
        !parsed.user.id ||
        !parsed.user.email ||
        !parsed.user.role
      ) {
        localStorage.removeItem(
          SESSION_KEY
        );

        setLoading(false);
        return;
      }

      /*
       * Validate dentist session.
       */
      if (
        parsed.user.role === 'DENTIST'
      ) {
        const dentist =
          parsed.dentist;

        if (
          !dentist ||
          !dentist.id ||
          typeof dentist.firstName !==
            'string' ||
          typeof dentist.lastName !==
            'string'
        ) {
          console.log(
            'Removing invalid saved dentist session'
          );

          localStorage.removeItem(
            SESSION_KEY
          );

          setLoading(false);
          return;
        }
      }

      /*
       * Validate client session.
       */
      if (
        parsed.user.role === 'CLIENT'
      ) {
        const client =
          parsed.client;

        if (
          !client ||
          !client.id ||
          typeof client.firstName !==
            'string' ||
          typeof client.lastName !==
            'string'
        ) {
          console.log(
            'Removing invalid saved client session'
          );

          localStorage.removeItem(
            SESSION_KEY
          );

          setLoading(false);
          return;
        }
      }

      console.log(
        'Loaded saved session:',
        parsed
      );

      setSession(parsed);

    } catch (error) {

      console.error(
        'Failed to parse saved session:',
        error
      );

      localStorage.removeItem(
        SESSION_KEY
      );
    }

    setLoading(false);
  }, []);

  /*
   * Login.
   */
  async function login(
    email: string,
    password: string
  ) {

    const result =
      await authApi.login({
        email,
        password,
      });

    console.log(
      'LOGIN RESULT:',
      result
    );

    /*
     * Save React session.
     */
    setSession(result);

    /*
     * Save the same correctly mapped
     * object into localStorage.
     */
    localStorage.setItem(
      SESSION_KEY,
      JSON.stringify(result)
    );
  }

  /*
   * Logout.
   */
  function logout() {

    setSession(null);

    localStorage.removeItem(
      SESSION_KEY
    );

    localStorage.removeItem(
      'token'
    );
  }

  return (
    <AuthContext.Provider
      value={{
        session,
        loading,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {

  const context =
    useContext(AuthContext);

  if (!context) {
    throw new Error(
      'useAuth must be used inside AuthProvider'
    );
  }

  return context;
}