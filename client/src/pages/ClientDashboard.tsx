import {
  useEffect,
  useRef,
  useState,
} from 'react';

import type {
  Client,
  Appointment,
  Dentist,
  Treatment,
  MedicalRecord,
} from '../types';

import { useAuth } from '../components/context/AuthContext';

import { clientApi } from '../api/clientApi';
import { dentistApi } from '../api/dentistApi';
import { treatmentApi } from '../api/treatmentApi';
import { medicalRecordApi } from '../api/medicalRecordApi';
import { chatApi } from '../api/chatApi';

import type {
  Conversation,
  ChatMessage,
} from '../api/chatApi';

import Sidebar from '../components/Layout/Sidebar';
import Topbar from '../components/Layout/Topbar';

import {
  dentistName,
  treatmentName,
  formatDate,
  formatTime,
} from '../utils/helpers';

interface Props {
  client: Client;
}

export default function ClientDashboard({
  client,
}: Props) {
  const { logout } = useAuth();

  // =========================================================
  // PAGE
  // =========================================================

  const [page, setPage] =
    useState<string>('dashboard');

  // =========================================================
  // DATA
  // =========================================================

  const [appointments, setAppointments] =
    useState<Appointment[]>([]);

  const [dentists, setDentists] =
    useState<Dentist[]>([]);

  const [treatments, setTreatments] =
    useState<Treatment[]>([]);

  const [records, setRecords] =
    useState<MedicalRecord[]>([]);

  // =========================================================
  // CHAT
  // =========================================================

  
    const [, setConversations] =
  useState<Conversation[]>([]);

  const [selectedConversation, setSelectedConversation] =
    useState<Conversation | null>(null);

  const [messages, setMessages] =
    useState<ChatMessage[]>([]);

  const [messageText, setMessageText] =
    useState<string>('');

  // =========================================================
  // LOADING
  // =========================================================

  const [loading, setLoading] =
    useState<boolean>(true);

  const [recordsLoading, setRecordsLoading] =
    useState<boolean>(false);

  const [messagesLoading, setMessagesLoading] =
    useState<boolean>(false);

  const [sendingMessage, setSendingMessage] =
    useState<boolean>(false);

  const [error, setError] =
    useState<string>('');

  const [dentistSearch, setDentistSearch] =
  useState('');

const [dentistSpecialty, setDentistSpecialty] =
  useState('');

  // =========================================================
  // AUTO SCROLL
  // =========================================================

  const messagesEndRef =
    useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (
      page !== 'messages' ||
      selectedConversation === null
    ) {
      return;
    }

    messagesEndRef.current?.scrollIntoView({
      behavior: 'smooth',
      block: 'end',
    });
  }, [
    messages,
    page,
    selectedConversation,
  ]);

  // =========================================================
  // LOAD DASHBOARD
  // =========================================================

  useEffect(() => {
    async function loadDashboard() {
      try {
        setLoading(true);
        setError('');

        const [
          appointmentData,
          dentistData,
          treatmentData,
        ] = await Promise.all([
          clientApi.getAppointments(),
          dentistApi.getAll(),
          treatmentApi.getAll(),
        ]);

        setAppointments(appointmentData);
        setDentists(dentistData);
        setTreatments(treatmentData);
      } catch (err) {
        console.error(
          'Failed to load dashboard:',
          err
        );

        setError(
          err instanceof Error
            ? err.message
            : 'Failed to load dashboard'
        );
      } finally {
        setLoading(false);
      }
    }

    loadDashboard();
  }, [client.id]);

  // =========================================================
  // LOAD MEDICAL RECORDS
  // =========================================================

  useEffect(() => {
    if (page !== 'records') {
      return;
    }

    async function loadRecords() {
      try {
        setRecordsLoading(true);
        setError('');

        const data =
          await medicalRecordApi.getByClient(
            String(client.id)
          );

        setRecords(data);
      } catch (err) {
        console.error(
          'Failed to load medical records:',
          err
        );

        setError(
          err instanceof Error
            ? err.message
            : 'Failed to load medical records'
        );
      } finally {
        setRecordsLoading(false);
      }
    }

    loadRecords();
  }, [page, client.id]);

  // =========================================================
  // LOAD CONVERSATIONS
  // =========================================================

  useEffect(() => {
    if (page !== 'messages') {
      return;
    }

    async function loadConversations() {
      try {
        setMessagesLoading(true);
        setError('');

        const data =
          await chatApi.getConversations();

        setConversations(data);
      } catch (err) {
        console.error(
          'Failed to load conversations:',
          err
        );

        setError(
          err instanceof Error
            ? err.message
            : 'Failed to load conversations'
        );
      } finally {
        setMessagesLoading(false);
      }
    }

    loadConversations();
  }, [page]);

  // =========================================================
  // LOAD MESSAGES
  // =========================================================

  useEffect(() => {
    if (
      page !== 'messages' ||
      selectedConversation === null
    ) {
      return;
    }

    // IMPORTANT:
    // Store the conversation in a local constant.
    // This prevents TypeScript from considering it
    // possibly null inside the async function.

    const conversation =
      selectedConversation;

    async function loadMessages() {
      try {
        setMessagesLoading(true);
        setError('');

        const data =
          await chatApi.getMessages(
            conversation.id
          );

        setMessages(data);
      } catch (err) {
        console.error(
          'Failed to load messages:',
          err
        );

        setError(
          err instanceof Error
            ? err.message
            : 'Failed to load messages'
        );
      } finally {
        setMessagesLoading(false);
      }
    }

    loadMessages();
  }, [
    page,
    selectedConversation,
  ]);

  // =========================================================
  // SEND MESSAGE
  // =========================================================

  async function handleSendMessage() {
    const content =
      messageText.trim();

    if (
      selectedConversation === null ||
      !content ||
      sendingMessage
    ) {
      return;
    }

    try {
      setSendingMessage(true);
      setError('');

      const newMessage =
        await chatApi.sendMessage(
          selectedConversation.id,
          content
        );

      setMessages(previous => [
        ...previous,
        newMessage,
      ]);

      setMessageText('');
    } catch (err) {
      console.error(
        'Failed to send message:',
        err
      );

      setError(
        err instanceof Error
          ? err.message
          : 'Failed to send message'
      );
    } finally {
      setSendingMessage(false);
    }
  }

  // =========================================================
  // UPCOMING APPOINTMENTS
  // =========================================================

  const upcoming =
    appointments
      .filter(
        appointment =>
          appointment.status === 'PENDING' ||
          appointment.status === 'ACCEPTED'
      )
      .sort((a, b) =>
        `${a.appointmentDate}${a.startTime}`
          .localeCompare(
            `${b.appointmentDate}${b.startTime}`
          )
      );

  // =========================================================
  // OPEN CONVERSATION
  // =========================================================

  async function openConversation(
    dentist: Dentist
  ) {
    try {
      setPage('messages');
      setError('');
      setMessagesLoading(true);

      if (
        dentist.userId === undefined ||
        dentist.userId === null ||
        dentist.userId === ''
      ) {
        throw new Error(
          'This dentist does not have a valid user ID.'
        );
      }

      const conversation =
        await chatApi.createOrGetConversation(
          Number(dentist.userId)
        );

      setConversations(current => {
        const exists =
          current.some(
            item =>
              String(item.id) ===
              String(conversation.id)
          );

        if (exists) {
          return current.map(item =>
            String(item.id) ===
            String(conversation.id)
              ? conversation
              : item
          );
        }

        return [
          conversation,
          ...current,
        ];
      });

      setMessages([]);

      setSelectedConversation(
        conversation
      );
    } catch (err) {
      console.error(
        'Failed to open conversation:',
        err
      );

      setError(
        err instanceof Error
          ? err.message
          : 'Failed to open conversation'
      );
    } finally {
      setMessagesLoading(false);
    }
  }

  // =========================================================
  // FIND DENTIST FOR CONVERSATION
  // =========================================================

  function getSelectedDentist():
    Dentist | undefined {
    if (
      selectedConversation === null
    ) {
      return undefined;
    }

    return dentists.find(
      dentist =>
        Number(dentist.id) ===
        Number(
          selectedConversation.dentistId
        )
    );
  }


  const filteredDentists =
  dentists.filter(dentist => {

    const search =
      dentistSearch
        .trim()
        .toLowerCase();

    const matchesSearch =
      !search ||
      `${dentist.firstName} ${dentist.lastName}`
        .toLowerCase()
        .includes(search) ||
      (
        dentist.specialization || ''
      )
        .toLowerCase()
        .includes(search) ||
      (
        dentist.clinicName || ''
      )
        .toLowerCase()
        .includes(search);

    const matchesSpecialty =
      !dentistSpecialty ||
      dentist.specialization ===
        dentistSpecialty;

    return (
      matchesSearch &&
      matchesSpecialty
    );
  });
  // =========================================================
  // RENDER
  // =========================================================

  return (
    <div className="app-shell">

      {/* =====================================================
          SIDEBAR
      ===================================================== */}

      <Sidebar
        role="CLIENT"
        active={page}
        onNavigate={setPage}
        onLogout={logout}
      />

      {/* =====================================================
          MAIN
      ===================================================== */}

      <main className="main-area">

        <Topbar
          title={
            `Good day, ${client.firstName}`
          }
          subtitle="Here's what's happening with your dental care."
        />

        <div className="content">

          {/* =================================================
              LOADING
          ================================================= */}

          {loading && (
            <div className="card loading-card">
              Loading your dashboard...
            </div>
          )}

          {/* =================================================
              ERROR
          ================================================= */}

          {error && (
            <div className="card error-card">
              {error}
            </div>
          )}

          {/* =================================================
              CONTENT
          ================================================= */}

          {!loading && (
            <>

              {page === 'dashboard' && (
  <>

    {/* =================================================
        DASHBOARD HEADER
    ================================================= */}

    <div className="section-header">

      <div>
        <p className="eyebrow">
          Patient overview
        </p>

        <h3>
          Your dental care
        </h3>

        <p className="section-description">
          Keep track of your appointments, visits and
          dental care in one place.
        </p>
      </div>

      <button
        type="button"
        className="btn btn-primary btn-sm"
        onClick={() => setPage('dentists')}
      >
        Find a dentist
      </button>

    </div>


    {/* =================================================
        STATS
    ================================================= */}

    <div className="grid grid-3">

      <div className="card stat-card">

        <div className="stat-number">
          {appointments.length}
        </div>

        <div className="stat-label">
          Total appointments
        </div>

      </div>


      <div className="card stat-card">

        <div className="stat-number">
          {
            appointments.filter(
              appointment =>
                appointment.status === 'PENDING'
            ).length
          }
        </div>

        <div className="stat-label">
          Pending appointments
        </div>

      </div>


      <div className="card stat-card">

        <div className="stat-number">
          {
            appointments.filter(
              appointment =>
                appointment.status === 'COMPLETED'
            ).length
          }
        </div>

        <div className="stat-label">
          Completed visits
        </div>

      </div>

    </div>


    {/* =================================================
        QUICK SUMMARY
    ================================================= */}

    <div className="dashboard-summary">

      <div className="dashboard-summary-item">

        <span>
          Upcoming
        </span>

        <strong>
          {upcoming.length}
        </strong>

      </div>

      <div className="dashboard-summary-divider" />

      <div className="dashboard-summary-item">

        <span>
          Completed
        </span>

        <strong>
          {
            appointments.filter(
              appointment =>
                appointment.status ===
                'COMPLETED'
            ).length
          }
        </strong>

      </div>

      <div className="dashboard-summary-divider" />

      <div className="dashboard-summary-item">

        <span>
          Medical records
        </span>

        <strong>
          {records.length}
        </strong>

      </div>

    </div>


    {/* =================================================
        UPCOMING APPOINTMENTS
    ================================================= */}

    <div className="section-header section-spaced">

      <div>

        <p className="eyebrow">
          Schedule
        </p>

        <h3>
          Upcoming appointments
        </h3>

      </div>

      <button
        type="button"
        className="btn btn-ghost btn-sm"
        onClick={() =>
          setPage('appointments')
        }
      >
        View all
      </button>

    </div>


    <div className="card appointments-card">

      {upcoming.length === 0 ? (

        <div className="empty-state">

          <h4>
            No upcoming appointments
          </h4>

          <p>
            Book your next dental visit when
            you're ready.
          </p>

          <button
            type="button"
            className="btn btn-primary btn-sm"
            onClick={() =>
              setPage('dentists')
            }
          >
            Find a dentist
          </button>

        </div>

      ) : (

        <div className="appointment-list">

          {upcoming
            .slice(0, 5)
            .map(appointment => (

              <div
                className="appointment-row"
                key={appointment.id}
              >

                {/* DATE */}

                <div className="appointment-date">

                  <span className="appointment-date-day">
                    {formatDate(
                      appointment.appointmentDate
                    )}
                  </span>

                  <span className="appointment-time">
                    {formatTime(
                      appointment.startTime
                    )}
                    {' – '}
                    {formatTime(
                      appointment.endTime
                    )}
                  </span>

                </div>


                {/* DENTIST */}

                <div className="appointment-patient">

                  <div className="avatar appointment-avatar">

                    {(() => {

                      const dentist =
                        dentists.find(
                          item =>
                            String(item.id) ===
                            String(
                              appointment.dentistId
                            )
                        );

                      return (
                        <>
                          {dentist?.firstName?.[0] ?? ''}
                          {dentist?.lastName?.[0] ?? ''}
                        </>
                      );

                    })()}

                  </div>


                  <div>

                    <strong>
                      {dentistName(
                        dentists,
                        appointment.dentistId
                      )}
                    </strong>

                    <span>
                      Dentist
                    </span>

                  </div>

                </div>


                {/* TREATMENT */}

                <div className="appointment-treatment">

                  <strong>
                    {treatmentName(
                      treatments,
                      appointment.treatmentId
                    )}
                  </strong>

                </div>


                {/* STATUS */}

                <div className="appointment-status">

                  <span
                    className={
                      `badge badge-${appointment.status}`
                    }
                  >
                    {appointment.status}
                  </span>

                </div>


                {/* ACTION */}

                <div className="appointment-actions">

                  <button
                    type="button"
                    className="btn btn-sm btn-ghost"
                    onClick={() =>
                      openConversation(
                        dentists.find(
                          dentist =>
                            String(dentist.id) ===
                            String(
                              appointment.dentistId
                            )
                        )!
                      )
                    }
                  >
                    Message
                  </button>

                </div>

              </div>

            ))}

        </div>

      )}

    </div>


    {/* =================================================
        CARE QUICK ACTIONS
    ================================================= */}

    <div className="section-header section-spaced">

      <div>

        <p className="eyebrow">
          Quick access
        </p>

        <h3>
          Your dental care
        </h3>

      </div>

    </div>


    <div className="grid grid-3">

      <button
        type="button"
        className="card dashboard-action-card"
        onClick={() =>
          setPage('dentists')
        }
      >

        <strong>
          Find a dentist
        </strong>

        <span>
          Browse dentists and specialties
        </span>

      </button>


      <button
        type="button"
        className="card dashboard-action-card"
        onClick={() =>
          setPage('messages')
        }
      >

        <strong>
          Messages
        </strong>

        <span>
          Talk directly with your dentist
        </span>

      </button>


      <button
        type="button"
        className="card dashboard-action-card"
        onClick={() =>
          setPage('records')
        }
      >

        <strong>
          Medical records
        </strong>

        <span>
          Review your dental history
        </span>

      </button>

    </div>

  </>
)}

              {/* =================================================
                  APPOINTMENTS
              ================================================= */}

              {page === 'appointments' && (
                <div className="card">

                  <div className="section-header">
                    <h3>
                      My appointments
                    </h3>
                  </div>

                  {appointments.length === 0 ? (

                    <div className="empty-state">
                      No appointments found.
                    </div>

                  ) : (

                    appointments.map(
                      appointment => (

                        <div
                          className="list-row"
                          key={appointment.id}
                        >

                          <div>
                            <strong>
                              {formatDate(
                                appointment.appointmentDate
                              )}
                            </strong>

                            <div className="muted">
                              {formatTime(
                                appointment.startTime
                              )}
                              {' – '}
                              {formatTime(
                                appointment.endTime
                              )}
                            </div>
                          </div>

                          <div>
                            <strong>
                              {dentistName(
                                dentists,
                                appointment.dentistId
                              )}
                            </strong>
                          </div>

                          <div>
                            {treatmentName(
                              treatments,
                              appointment.treatmentId
                            )}
                          </div>

                          <span
                            className={
                              `badge badge-${appointment.status}`
                            }
                          >
                            {appointment.status}
                          </span>

                        </div>

                      )
                    )

                  )}

                </div>
              )}

              {/* =================================================
                  DENTISTS
              ================================================= */}

              {page === 'dentists' && (
  <div className="dentists-page">

    {/* PAGE INTRO */}
    <div className="dentists-intro">
      <div>
        <p className="eyebrow">
          Find care
        </p>

        <h3>
          Find a dentist
        </h3>

        <p className="dentists-intro-text">
          Browse our dental professionals and find
          the right dentist for your care.
        </p>
      </div>

      <div className="dentists-count">
        <strong>{filteredDentists.length}</strong>
        <span>
          {dentists.length === 1
            ? 'dentist available'
            : 'dentists available'}
        </span>
      </div>
    </div>

    {/* SEARCH / FILTERS */}
    <div className="dentist-filters">

      <div className="dentist-search">
        <svg
          viewBox="0 0 24 24"
          aria-hidden="true"
        >
          <circle
            cx="11"
            cy="11"
            r="6.5"
          />
          <path d="m16 16 5 5" />
        </svg>

        <input
  type="text"
  value={dentistSearch}
  onChange={event =>
    setDentistSearch(event.target.value)
  }
  placeholder="Search by name, specialty or clinic..."
/>
      </div>

      <select
  className="dentist-specialty-filter"
  value={dentistSpecialty}
  onChange={event =>
    setDentistSpecialty(event.target.value)
  }
>
        <option value="">
          All specialties
        </option>

        {Array.from(
          new Set(
            dentists
              .map(
                dentist =>
                  dentist.specialization
              )
              .filter(Boolean)
          )
        ).map(specialization => (
          <option
            key={specialization}
            value={specialization}
          >
            {specialization}
          </option>
        ))}
      </select>

    </div>

    {/* DENTIST GRID */}
    {dentists.length === 0 ? (

      <div className="card">
        <div className="empty-state">
          <h4>
            No dentists available
          </h4>

          <p>
            There are currently no dental
            professionals to display.
          </p>
        </div>
      </div>

    ) : (

      <div className="grid grid-2 dentist-grid">

        {filteredDentists.map(dentist => (

          <article
            className="card dentist-card"
            key={dentist.id}
          >

            {/* HEADER */}
            <div className="dentist-card-header">

              <div className="dentist-avatar">
                {dentist.firstName?.[0] ?? ''}
                {dentist.lastName?.[0] ?? ''}
              </div>

              <div className="dentist-identity">

                <h3>
                  Dr. {dentist.firstName}{' '}
                  {dentist.lastName}
                </h3>

                <p>
                  {dentist.specialization ||
                    'General Dentistry'}
                </p>

              </div>

            </div>

            {/* CLINIC */}
            <div className="dentist-clinic">

              <span className="dentist-clinic-icon">
                <svg
                  viewBox="0 0 24 24"
                  aria-hidden="true"
                >
                  <path d="M4 21V5l8-3 8 3v16" />
                  <path d="M8 9h2M14 9h2M8 13h2M14 13h2" />
                  <path d="M10 21v-4h4v4" />
                </svg>
              </span>

              <span>
                {dentist.clinicName ||
                  'Clinic not specified'}
              </span>

            </div>

            {/* EXPERIENCE */}
            <div className="dentist-experience">

              <span className="dentist-experience-value">
                {dentist.experienceYears ?? 0}
              </span>

              <span>
                years of experience
              </span>

            </div>

            {/* BIO */}
            <p className="dentist-bio">
              {dentist.biography ||
                'This dentist has not added a biography yet.'}
            </p>

            {/* ACTIONS */}
            <div className="dentist-actions">

              <button
                type="button"
                className="btn btn-ghost btn-sm"
                onClick={() => openConversation(dentist)}
              >
                Message
              </button>

              <button
                type="button"
                className="btn btn-primary btn-sm"
                onClick={() => setPage('appointments')}
              >
                Book appointment
              </button>

            </div>

          </article>

        ))}

      </div>

    )}

  </div>
)}

              {/* =================================================
                  MEDICAL RECORDS
              ================================================= */}

              {page === 'records' && (
                <div>

                  <div className="section-header">
                    <div>
                      <p className="eyebrow">
                        Health history
                      </p>

                      <h3>
                        Medical Records
                      </h3>
                    </div>
                  </div>

                  {recordsLoading ? (

                    <div className="card loading-card">
                      Loading medical records...
                    </div>

                  ) : records.length === 0 ? (

                    <div className="card">

                      <div className="empty-state">

                        <h4>
                          No medical records
                        </h4>

                        <p>
                          Your dentist has not added
                          any medical records yet.
                        </p>

                      </div>

                    </div>

                  ) : (

                    <div className="grid grid-2">

                      {records.map(record => (

                        <div
                          className="card"
                          key={record.id}
                        >

                          <div className="section-header">

                            <div>

                              <p className="eyebrow">
                                Visit
                              </p>

                              <h3>
                                {formatDate(
                                  record.createdAt
                                )}
                              </h3>

                            </div>

                            <span className="badge">
                              Medical Record
                            </span>

                          </div>

                          <div className="profile-grid">

                            <div>

                              <span>
                                Diagnosis
                              </span>

                              <strong>
                                {record.diagnosis ||
                                  'Not provided'}
                              </strong>

                            </div>

                            <div>

                              <span>
                                Treatment performed
                              </span>

                              <strong>
                                {record.treatmentPerformed ||
                                  'Not provided'}
                              </strong>

                            </div>

                          </div>

                          <div
                            style={{
                              marginTop: '20px',
                            }}
                          >

                            <span className="muted">
                              Notes
                            </span>

                            <p>
                              {record.notes ||
                                'No notes provided.'}
                            </p>

                          </div>

                        </div>

                      ))}

                    </div>

                  )}

                </div>
              )}

              {/* =================================================
                  MESSAGES
              ================================================= */}

              {page === 'messages' && (
                <div>

                  <div className="section-header">

                    <div>

                      <p className="eyebrow">
                        Communication
                      </p>

                      <h3>
                        Messages
                      </h3>

                    </div>

                  </div>

                  <div
                    className="card"
                    style={{
                      display: 'grid',
                      gridTemplateColumns:
                        '280px minmax(0, 1fr)',
                      height: '650px',
                      minHeight: '650px',
                      padding: 0,
                      overflow: 'hidden',
                    }}
                  >

                    {/* =================================================
                        DENTIST LIST
                    ================================================= */}

                    <div
                      style={{
                        borderRight:
                          '1px solid #e5e7eb',
                        padding: '16px',
                        overflowY: 'auto',
                      }}
                    >

                      <h4>
                        Dentists
                      </h4>

                      {dentists.length === 0 ? (

                        <div className="empty-state">
                          <p>
                            No dentists found.
                          </p>
                        </div>

                      ) : (

                        dentists.map(dentist => {

                          const active =
                            selectedConversation !== null &&
                            Number(
                              selectedConversation.dentistId
                            ) ===
                            Number(dentist.id);

                          return (

                            <button
                              key={dentist.id}
                              type="button"
                              onClick={() =>
                                openConversation(
                                  dentist
                                )
                              }
                              style={{
                                display: 'flex',
                                alignItems: 'center',
                                gap: '12px',
                                width: '100%',
                                textAlign: 'left',
                                padding: '12px',
                                marginTop: '8px',
                                border: 'none',
                                borderRadius: '8px',
                                cursor: 'pointer',
                                background:
                                  active
                                    ? '#eef2ff'
                                    : 'transparent',
                              }}
                            >

                              <div className="avatar">
                                {dentist.firstName?.[0] ?? ''}
                                {dentist.lastName?.[0] ?? ''}
                              </div>

                              <div>

                                <strong>
                                  Dr. {dentist.firstName}{' '}
                                  {dentist.lastName}
                                </strong>

                                <div className="muted">
                                  {dentist.specialization ||
                                    'General Dentistry'}
                                </div>

                                <div className="muted">
                                  Message dentist
                                </div>

                              </div>

                            </button>

                          );
                        })

                      )}

                    </div>

                    {/* =================================================
                        CHAT
                    ================================================= */}

                    <div
                      style={{
                        display: 'flex',
                        flexDirection: 'column',
                        minWidth: 0,
                        minHeight: 0,
                      }}
                    >

                      {/* =================================================
                          NO SELECTED CONVERSATION
                      ================================================= */}

                      {selectedConversation === null ? (

                        <div
                          className="empty-state"
                          style={{
                            margin: 'auto',
                          }}
                        >

                          <h4>
                            Select a dentist
                          </h4>

                          <p>
                            Choose a dentist to start
                            a conversation.
                          </p>

                        </div>

                      ) : (

                        <>

                          {/* =================================================
                              CHAT HEADER
                          ================================================= */}

                          <div
                            style={{
                              display: 'flex',
                              alignItems: 'center',
                              gap: '12px',
                              padding: '16px',
                              flexShrink: 0,
                              borderBottom:
                                '1px solid #e5e7eb',
                            }}
                          >

                            {(() => {

                              const dentist =
                                getSelectedDentist();

                              return (
                                <>
                                  <div className="avatar">

                                    {dentist?.firstName?.[0] ?? ''}

                                    {dentist?.lastName?.[0] ?? ''}

                                  </div>

                                  <div>

                                    <strong>

                                      {dentist
                                        ? `Dr. ${dentist.firstName} ${dentist.lastName}`
                                        : 'Dentist'}

                                    </strong>

                                    <div className="muted">
                                      Dentist
                                    </div>

                                  </div>
                                </>
                              );

                            })()}

                          </div>

                          {/* =================================================
                              MESSAGE AREA
                          ================================================= */}

                          <div
                            style={{
                              flex: 1,
                              minHeight: 0,
                              padding: '20px',
                              overflowY: 'auto',
                              display: 'flex',
                              flexDirection: 'column',
                              gap: '8px',
                            }}
                          >

                            {messagesLoading ? (

                              <div className="empty-state">
                                Loading messages...
                              </div>

                            ) : messages.length === 0 ? (

                              <div className="empty-state">

                                <h4>
                                  No messages yet
                                </h4>

                                <p>
                                  Start the conversation
                                  with your dentist.
                                </p>

                              </div>

                            ) : (

                              messages.map(message => {

                                const isMine =
                                  Number(
                                    message.senderId
                                  ) ===
                                  Number(
                                    client.userId
                                  );

                                return (

                                  <div
                                    key={message.id}
                                    style={{
                                      display: 'flex',
                                      justifyContent:
                                        isMine
                                          ? 'flex-end'
                                          : 'flex-start',
                                      width: '100%',
                                      flexShrink: 0,
                                    }}
                                  >

                                    <div
                                      style={{
                                        maxWidth: '70%',
                                        padding:
                                          '10px 14px',
                                        borderRadius:
                                          '14px',
                                        background:
                                          isMine
                                            ? '#2563eb'
                                            : '#e5e7eb',
                                        color:
                                          isMine
                                            ? '#ffffff'
                                            : '#1f2937',
                                        wordBreak:
                                          'break-word',
                                      }}
                                    >

                                      <div>
                                        {message.content}
                                      </div>

                                      <div
                                        style={{
                                          marginTop:
                                            '5px',
                                          fontSize:
                                            '11px',
                                          opacity:
                                            0.75,
                                        }}
                                      >

                                        {new Date(
                                          message.sentAt
                                        ).toLocaleString()}

                                      </div>

                                    </div>

                                  </div>

                                );
                              })

                            )}

                            <div
                              ref={messagesEndRef}
                              style={{
                                height: 1,
                                flexShrink: 0,
                              }}
                            />

                          </div>

                          {/* =================================================
    MESSAGE INPUT
================================================= */}

<div className="chat-composer">

  <input
    type="text"
    value={messageText}
    placeholder="Write a message..."
    onChange={event =>
      setMessageText(
        event.target.value
      )
    }
    onKeyDown={event => {

      if (
        event.key === 'Enter' &&
        !event.shiftKey
      ) {
        event.preventDefault();
        handleSendMessage();
      }

    }}
    disabled={sendingMessage}
    className="chat-input"
  />

  <button
    type="button"
    className="btn btn-primary chat-send-button"
    onClick={handleSendMessage}
    disabled={
      sendingMessage ||
      !messageText.trim()
    }
  >

    <svg
      viewBox="0 0 24 24"
      aria-hidden="true"
    >
      <path d="M22 2 11 13" />
      <path d="m22 2-7 20-4-9-9-4Z" />
    </svg>

    <span>
      {sendingMessage
        ? 'Sending...'
        : 'Send'}
    </span>

  </button>

</div>

                        </>

                      )}

                    </div>

                  </div>

                </div>
              )}

              {/* =================================================
    PROFILE
================================================= */}

{page === 'profile' && (
  <div className="profile-page">

    {/* PROFILE HEADER */}
    <div className="card profile-hero">

      <div className="profile-avatar">
        {client.profileImage ? (
          <img
            src={client.profileImage}
            alt={`${client.firstName} ${client.lastName}`}
          />
        ) : (
          <>
            {client.firstName?.[0] ?? ''}
            {client.lastName?.[0] ?? ''}
          </>
        )}
      </div>

      <div className="profile-hero-info">

        <p className="eyebrow">
          Patient profile
        </p>

        <h2>
          {client.firstName} {client.lastName}
        </h2>

        <div className="profile-status">
          <span className="profile-status-dot" />
          Active patient
        </div>

      </div>

    </div>


    {/* CONTACT INFORMATION */}
    <div className="profile-section">

      <div className="profile-section-header">
        <div>
          <p className="eyebrow">
            Contact
          </p>

          <h3>
            Contact information
          </h3>
        </div>
      </div>

      <div className="card profile-info-card">

        <div className="profile-info-item">

          <div className="profile-info-icon">
            <svg
              viewBox="0 0 24 24"
              aria-hidden="true"
            >
              <path d="M5 4h4l2 5-2.5 1.5a13 13 0 0 0 5 5L15 13l5 2v4c0 1-1 1-2 1C10 20 4 14 4 6c0-1 .5-2 1-2Z" />
            </svg>
          </div>

          <div>
            <span>
              Phone number
            </span>

            <strong>
              {client.phone || 'Not provided'}
            </strong>
          </div>

        </div>

        <div className="profile-info-item">

          <div className="profile-info-icon">
            <svg
              viewBox="0 0 24 24"
              aria-hidden="true"
            >
              <rect
                x="4"
                y="5"
                width="16"
                height="15"
                rx="2"
              />

              <path d="M8 3v4M16 3v4M4 10h16" />
            </svg>
          </div>

          <div>
            <span>
              Date of birth
            </span>

            <strong>
              {client.dateOfBirth || 'Not provided'}
            </strong>
          </div>

        </div>

      </div>

    </div>


    {/* PERSONAL INFORMATION */}
    <div className="profile-section">

      <div className="profile-section-header">
        <div>
          <p className="eyebrow">
            Personal details
          </p>

          <h3>
            Personal information
          </h3>
        </div>
      </div>

      <div className="card profile-details-card">

        <div className="profile-detail">

          <span>
            First name
          </span>

          <strong>
            {client.firstName}
          </strong>

        </div>

        <div className="profile-detail">

          <span>
            Last name
          </span>

          <strong>
            {client.lastName}
          </strong>

        </div>

        <div className="profile-detail">

          <span>
            Date of birth
          </span>

          <strong>
            {client.dateOfBirth || 'Not provided'}
          </strong>

        </div>

        <div className="profile-detail">

          <span>
            Patient ID
          </span>

          <strong>
            #{client.id}
          </strong>

        </div>

      </div>

    </div>

  </div>
)}

            </>
          )}

        </div>

      </main>

    </div>
  );
}