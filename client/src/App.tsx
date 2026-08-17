import { useAuth } from './components/context/AuthContext';

import Login from './components/Auth/Login';

import ClientDashboard from './pages/ClientDashboard';
import DentistDashboard from './pages/DentistDashboard';

export default function App() {
  const {
    session,
    loading,
  } = useAuth();

  if (loading) {
    return (
      <div className="loading-screen">
        Loading...
      </div>
    );
  }

  if (!session) {
    return <Login />;
  }

  if (
    session.user &&
    session.user.role === 'CLIENT' &&
    session.client
  ) {
    return (
      <ClientDashboard
        client={session.client}
      />
    );
  }

  if (
    session.user &&
    session.user.role === 'DENTIST' &&
    session.dentist
  ) {
    return (
      <DentistDashboard
        dentist={session.dentist}
      />
    );
  }

  return (
    <div className="auth-screen">
      <div className="auth-panel">
        <p className="eyebrow">
          Account
        </p>

        <h1>No profile found</h1>

        <p className="auth-sub">
          Your account exists, but the corresponding
          {session.user?.role === 'CLIENT'
            ? ' client'
            : ' dentist'}{' '}
          profile could not be found.
        </p>
      </div>
    </div>
  );
}