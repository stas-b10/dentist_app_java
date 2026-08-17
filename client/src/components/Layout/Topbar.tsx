interface Props {
  title: string;
  subtitle?: string;
}

export default function Topbar({
  title,
  subtitle,
}: Props) {
  return (
    <header className="topbar">
      <div className="topbar-title-area">
        <div className="topbar-title-accent" />

        <div>
          <span className="topbar-eyebrow">
            DentalCare Portal
          </span>

          <h2>{title}</h2>

          {subtitle && (
            <p>{subtitle}</p>
          )}
        </div>
      </div>

      <div className="topbar-actions">

        {/* Notifications */}
        <button
          type="button"
          className="topbar-notifications"
          aria-label="Notifications"
          title="Notifications"
        >
          <svg
            viewBox="0 0 24 24"
            aria-hidden="true"
          >
            <path d="M18 9a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9" />
            <path d="M10 21h4" />
          </svg>

          <span className="notification-dot" />
        </button>

        {/* Secure session */}
        <div className="topbar-status">
          <div className="topbar-status-icon">
            <svg
              viewBox="0 0 24 24"
              aria-hidden="true"
            >
              <path d="M7 10V7a5 5 0 0 1 10 0v3" />
              <rect
                x="5"
                y="10"
                width="14"
                height="10"
                rx="2"
              />
              <path d="M12 14v2" />
            </svg>
          </div>

          <div className="topbar-status-text">
            <strong>Secure</strong>
            <span>Protected session</span>
          </div>

          <span className="topbar-status-dot" />
        </div>
      </div>
    </header>
  );
}