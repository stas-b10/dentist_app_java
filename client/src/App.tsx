import './App.css'

function App() {
  return (
    <main className="app-shell">
      <section className="hero">
        <div className="hero-copy">
          <p className="eyebrow">Dentist App</p>
          <h1>Modern care for every patient visit</h1>
          <p>
            Manage appointments, treatment notes, and follow-up reminders from one calm workspace.
          </p>
          <div className="actions">
            <a className="primary" href="/appointments">
              View appointments
            </a>
            <a className="secondary" href="/records">
              Patient records
            </a>
          </div>
        </div>
      </section>
    </main>
  )
}

export default App
