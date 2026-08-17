import type { JSX } from 'react/jsx-runtime';
import type { Role } from '../../types';

interface Props {
  role: Role;
  active: string;
  onNavigate: (page: string) => void;
  onLogout: () => void;
}

const icons: Record<string, JSX.Element> = {
  dashboard: (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <rect x="4" y="4" width="6" height="6" rx="1" />
      <rect x="14" y="4" width="6" height="6" rx="1" />
      <rect x="4" y="14" width="6" height="6" rx="1" />
      <rect x="14" y="14" width="6" height="6" rx="1" />
    </svg>
  ),

  appointments: (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <rect x="4" y="5" width="16" height="15" rx="2" />
      <path d="M8 3v4M16 3v4M4 10h16" />
      <path d="M8 14h3M8 17h5" />
    </svg>
  ),

  dentists: (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <circle cx="12" cy="7" r="3.5" />
      <path d="M5 20a7 7 0 0 1 14 0" />
      <path d="M16.5 13.5 20 17l-3 3-2-2" />
    </svg>
  ),

  records: (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <path d="M6 3h9l4 4v14H6a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2Z" />
      <path d="M14 3v5h5M8 12h8M8 16h6" />
    </svg>
  ),

  patients: (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <circle cx="9" cy="8" r="3" />
      <path d="M3.5 20a5.5 5.5 0 0 1 11 0" />
      <circle cx="17" cy="9" r="2.5" />
      <path d="M15 15a5 5 0 0 1 5.5 5" />
    </svg>
  ),

  schedule: (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <circle cx="12" cy="12" r="8.5" />
      <path d="M12 7v5l3 2" />
    </svg>
  ),

  messages: (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <path d="M20 11.5a7.5 7.5 0 0 1-8 7.5 9 9 0 0 1-3-.5L4 20l1.5-3.5A7.5 7.5 0 1 1 20 11.5Z" />
      <path d="M8 12h.01M12 12h.01M16 12h.01" />
    </svg>
  ),

  profile: (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <circle cx="12" cy="8" r="3.5" />
      <path d="M5 20a7 7 0 0 1 14 0" />
    </svg>
  ),
};

export default function Sidebar({
  role,
  active,
  onNavigate,
  onLogout,
}: Props) {
  const clientItems = [
    ['dashboard', 'Dashboard'],
    ['appointments', 'Appointments'],
    ['dentists', 'Find a Dentist'],
    ['records', 'Medical Records'],
    ['messages', 'Messages'],
    ['profile', 'Profile'],
  ];

  const dentistItems = [
    ['dashboard', 'Dashboard'],
    ['appointments', 'Appointments'],
    ['patients', 'Patients'],
    ['schedule', 'Schedule'],
    ['messages', 'Messages'],
    ['profile', 'Profile'],
  ];

  const items =
    role === 'CLIENT'
      ? clientItems
      : dentistItems;

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <div className="brand-icon">
          <span>+</span>
        </div>

        <div className="brand-text">
          <strong>DentalCare</strong>
          <span>Healthcare Portal</span>
        </div>
      </div>

      <div className="sidebar-divider" />

      <div className="sidebar-menu">
        <div className="sidebar-menu-label">
  {role === 'CLIENT'
    ? 'My Care'
    : 'Practice'}
</div>

        <nav className="sidebar-nav">
          {items.map(([id, label]) => {
            const isActive = active === id;

            return (
              <button
                key={id}
                type="button"
                className={`sidebar-nav-item ${
                  isActive ? 'sidebar-nav-item-active' : ''
                }`}
                onClick={() => onNavigate(id)}
              >
                <span className="sidebar-nav-icon">
                  {icons[id]}
                </span>

                <span className="sidebar-nav-text">
                  {label}
                </span>

                {isActive && (
  <span className="sidebar-nav-active-dot" />
)}
              </button>
            );
          })}
        </nav>
      </div>

      <div className="sidebar-bottom">
        <div className="sidebar-user">
          <div className="sidebar-user-avatar">
            {role === 'CLIENT' ? 'P' : 'D'}
          </div>

          <div className="sidebar-user-info">
            <strong>
  {role === 'CLIENT'
    ? 'Patient'
    : 'Dentist'}
</strong>

<span>
  <i />
  {role === 'CLIENT'
    ? 'Patient portal'
    : 'Dental practice'}
</span>
          </div>
        </div>

        <button
          type="button"
          className="sidebar-logout"
          onClick={onLogout}
        >
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M10 5H6a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h4" />
            <path d="M14 8l4 4-4 4M9 12h9" />
          </svg>

          <span>Sign out</span>
        </button>
      </div>
    </aside>
  );
}