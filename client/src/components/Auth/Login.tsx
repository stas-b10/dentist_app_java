import { type FormEvent, useState } from 'react';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const { login } = useAuth();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();

    setError('');
    setLoading(true);

    try {
      await login(email, password);
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : 'Login failed'
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-screen">
      <div className="auth-panel">
        <div className="auth-logo">
          <div className="auth-logo-mark">
            +
          </div>
        </div>

        <p className="eyebrow">Dental care</p>

        <h1>Welcome back</h1>

        <p className="auth-sub">
          Sign in to manage your dental appointments
          and care.
        </p>

        <form
          className="auth-form"
          onSubmit={handleSubmit}
        >
          <label>
            Email

            <input
              type="email"
              value={email}
              onChange={(e) =>
                setEmail(e.target.value)
              }
              placeholder="you@example.com"
              required
            />
          </label>

          <label>
            Password

            <input
              type="password"
              value={password}
              onChange={(e) =>
                setPassword(e.target.value)
              }
              placeholder="••••••••"
              required
            />
          </label>

          {error && (
            <div className="form-error">
              {error}
            </div>
          )}

          <button
            className="btn btn-primary"
            disabled={loading}
            type="submit"
          >
            {loading ? 'Signing in...' : 'Sign in'}
          </button>
        </form>
      </div>
    </div>
  );
}