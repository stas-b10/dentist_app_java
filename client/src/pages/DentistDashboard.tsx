import { useEffect, useRef, useState } from 'react';

import type {
  Dentist,
  Appointment,
  Schedule,
  Client,
  Treatment,
} from '../types';

import { useAuth } from '../components/context/AuthContext';

import { dentistApi } from '../api/dentistApi';
import { appointmentApi } from '../api/appointmentApi';
import { chatApi } from '../api/chatApi';

import type {
  ChatMessage,
  Conversation,
} from '../api/chatApi';

import Sidebar from '../components/Layout/Sidebar';
import Topbar from '../components/Layout/Topbar';

import {
  clientName,
  treatmentName,
  formatDate,
  formatTime,
} from '../utils/helpers';

interface Props {
  dentist: Dentist;
}

export default function DentistDashboard({
  dentist,
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

  const [patients, setPatients] =
    useState<Client[]>([]);

  const [treatments, setTreatments] =
    useState<Treatment[]>([]);

  const [schedule, setSchedule] =
    useState<Schedule[]>([]);

  // =========================================================
  // LOADING / ERROR
  // =========================================================

  const [loading, setLoading] =
    useState<boolean>(true);

  const [error, setError] =
    useState<string>('');

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

  const [messagesLoading, setMessagesLoading] =
    useState<boolean>(false);

  const [sendingMessage, setSendingMessage] =
    useState<boolean>(false);

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
  // LOAD DENTIST DASHBOARD
  // =========================================================

  useEffect(() => {
    async function loadDashboard() {
      try {
        setLoading(true);
        setError('');

        const [
          appointmentData,
          patientData,
          treatmentData,
          scheduleData,
        ] = await Promise.all([
          dentistApi.getAppointments(dentist.id),
          dentistApi.getPatients(dentist.id),
          dentistApi.getTreatments(dentist.id),
          dentistApi.getSchedule(dentist.id),
        ]);

        setAppointments(appointmentData);
        setPatients(patientData);
        setTreatments(treatmentData);
        setSchedule(scheduleData);
      } catch (err) {
        console.error(
          'Failed to load dentist dashboard:',
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
  }, [dentist.id]);

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

    const conversationId =
      selectedConversation.id;

    async function loadMessages() {
      try {
        setMessagesLoading(true);
        setError('');

        const data =
          await chatApi.getMessages(
            conversationId
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
  // NAVIGATION
  // =========================================================

  function navigateTo(pageName: string) {
    setPage(pageName);
  }

  // =========================================================
  // CHANGE APPOINTMENT STATUS
  // =========================================================

  async function changeStatus(
    appointmentId: string,
    status:
      | 'ACCEPTED'
      | 'REJECTED'
      | 'COMPLETED'
  ) {
    try {
      setError('');

      let updated: Appointment;

      if (status === 'ACCEPTED') {
        updated =
          await appointmentApi.accept(
            appointmentId
          );
      } else if (status === 'REJECTED') {
        updated =
          await appointmentApi.reject(
            appointmentId
          );
      } else {
        updated =
          await appointmentApi.complete(
            appointmentId
          );
      }

      setAppointments(current =>
        current.map(item =>
          String(item.id) ===
          String(updated.id)
            ? updated
            : item
        )
      );
    } catch (err) {
      console.error(
        'Failed to update appointment:',
        err
      );

      setError(
        err instanceof Error
          ? err.message
          : 'Failed to update appointment'
      );
    }
  }

  // =========================================================
  // OPEN / CREATE CONVERSATION
  // =========================================================

  async function openConversation(
    patient: Client
  ) {
    try {
      setError('');
      setMessagesLoading(true);

      if (
        patient.userId === undefined ||
        patient.userId === null
      ) {
        throw new Error(
          'This patient does not have a valid user ID.'
        );
      }

      const conversation =
        await chatApi.createOrGetConversation(
          Number(patient.userId)
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

      /*
       * If this function is called from Patients,
       * automatically move to Messages.
       */
      setPage('messages');
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
  // GET PATIENT FOR CONVERSATION
  // =========================================================

  function getConversationPatient(
    conversation: Conversation
  ): Client | undefined {
    return patients.find(
      patient =>
        String(patient.userId) ===
        String(conversation.clientId)
    );
  }

  // =========================================================
  // GET PATIENT NAME
  // =========================================================

  function getConversationPatientName(
    conversation: Conversation
  ): string {
    const patient =
      getConversationPatient(
        conversation
      );

    if (!patient) {
      return 'Patient';
    }

    return `${patient.firstName} ${patient.lastName}`;
  }

  // =========================================================
  // SEND MESSAGE
  // =========================================================

  async function sendMessage() {
    const conversation =
      selectedConversation;

    const content =
      messageText.trim();

    if (
      conversation === null ||
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
          conversation.id,
          content
        );

      setMessages(current => [
        ...current,
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
  // APPOINTMENT ROW
  // =========================================================

  function AppointmentRow({
    appointment,
  }: {
    appointment: Appointment;
  }) {
    const patient =
      patients.find(
        item =>
          String(item.id) ===
          String(appointment.clientId)
      );

    const treatment =
      treatments.find(
        item =>
          String(item.id) ===
          String(appointment.treatmentId)
      );

    return (
      <div className="appointment-row">

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

        {/* PATIENT */}

        <div className="appointment-patient">

          <div className="avatar appointment-avatar">
            {patient?.firstName?.[0] ?? ''}
            {patient?.lastName?.[0] ?? ''}
          </div>

          <div>
            <strong>
              {patient
                ? `${patient.firstName} ${patient.lastName}`
                : clientName(
                    patients,
                    appointment.clientId
                  )}
            </strong>

            <span>
              {patient?.phone ||
                'Patient'}
            </span>
          </div>

        </div>

        {/* TREATMENT */}

        <div className="appointment-treatment">

          <strong>
            {treatment
              ? treatmentName(
                  treatments,
                  appointment.treatmentId
                )
              : treatmentName(
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

        {/* ACTIONS */}

        <div className="appointment-actions">

          {appointment.status ===
            'PENDING' && (
            <>
              <button
                type="button"
                className="btn btn-sm btn-primary"
                onClick={() =>
                  changeStatus(
                    appointment.id,
                    'ACCEPTED'
                  )
                }
              >
                Accept
              </button>

              <button
                type="button"
                className="btn btn-sm btn-danger"
                onClick={() =>
                  changeStatus(
                    appointment.id,
                    'REJECTED'
                  )
                }
              >
                Reject
              </button>
            </>
          )}

          {appointment.status ===
            'ACCEPTED' && (
            <button
              type="button"
              className="btn btn-sm btn-primary"
              onClick={() =>
                changeStatus(
                  appointment.id,
                  'COMPLETED'
                )
              }
            >
              Complete
            </button>
          )}

        </div>

      </div>
    );
  }

  // =========================================================
  // SCHEDULE ROW
  // =========================================================

  function ScheduleRow({
    item,
  }: {
    item: Schedule;
  }) {
    const day =
      item.dayOfWeek
        .charAt(0)
        .toUpperCase() +
      item.dayOfWeek
        .slice(1)
        .toLowerCase();

    return (
      <div className="schedule-row">

        <div className="schedule-day">

          <div className="schedule-day-icon">
            <svg
              viewBox="0 0 24 24"
              aria-hidden="true"
            >
              <circle
                cx="12"
                cy="12"
                r="8.5"
              />

              <path d="M12 7v5l3 2" />
            </svg>
          </div>

          <div>
            <strong>{day}</strong>

            <span>
              Weekly availability
            </span>
          </div>

        </div>

        <div className="schedule-time">

          <span className="schedule-time-label">
            Working hours
          </span>

          <strong>
            {formatTime(item.startTime)}
            {' – '}
            {formatTime(item.endTime)}
          </strong>

        </div>

        <div className="schedule-status">

          <span className="schedule-active">
            <i />
            Available
          </span>

        </div>

      </div>
    );
  }

  // =========================================================
  // STATS
  // =========================================================

  const pendingAppointments =
    appointments.filter(
      appointment =>
        appointment.status ===
        'PENDING'
    ).length;

  const acceptedAppointments =
    appointments.filter(
      appointment =>
        appointment.status ===
        'ACCEPTED'
    ).length;

  const completedAppointments =
    appointments.filter(
      appointment =>
        appointment.status ===
        'COMPLETED'
    ).length;

  // =========================================================
  // RENDER
  // =========================================================

  return (
    <div className="app-shell">

      {/* =====================================================
          SIDEBAR
          ===================================================== */}

      <Sidebar
        role="DENTIST"
        active={page}
        onNavigate={navigateTo}
        onLogout={logout}
      />

      {/* =====================================================
          MAIN
          ===================================================== */}

      <main className="main-area">

        <Topbar
          title={
            `Dr. ${dentist.firstName || ''} ${dentist.lastName || ''}`.trim()
          }
          subtitle={
            dentist.clinicName ||
            'Dental practice'
          }
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

          {!loading && (
            <>

              {/* =================================================
                  DASHBOARD
                  ================================================= */}

              {page === 'dashboard' && (
                <>

                  <div className="section-header">
                    <div>
                      <p className="eyebrow">
                        Practice overview
                      </p>

                      <h3>
                        Today's workspace
                      </h3>
                    </div>
                  </div>

                  {/* STATS */}

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
                        {patients.length}
                      </div>

                      <div className="stat-label">
                        Patients
                      </div>
                    </div>

                    <div className="card stat-card">
                      <div className="stat-number">
                        {pendingAppointments}
                      </div>

                      <div className="stat-label">
                        Awaiting confirmation
                      </div>
                    </div>

                  </div>

                  {/* QUICK SUMMARY */}

                  <div className="dashboard-summary">

                    <div className="dashboard-summary-item">

                      <span>
                        Accepted
                      </span>

                      <strong>
                        {acceptedAppointments}
                      </strong>

                    </div>

                    <div className="dashboard-summary-divider" />

                    <div className="dashboard-summary-item">

                      <span>
                        Completed
                      </span>

                      <strong>
                        {completedAppointments}
                      </strong>

                    </div>

                    <div className="dashboard-summary-divider" />

                    <div className="dashboard-summary-item">

                      <span>
                        Pending
                      </span>

                      <strong>
                        {pendingAppointments}
                      </strong>

                    </div>

                  </div>

                  {/* APPOINTMENTS */}

                  <div className="section-header section-spaced">

                    <div>
                      <p className="eyebrow">
                        Schedule
                      </p>

                      <h3>
                        Appointments
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

                    {appointments.length === 0 ? (

                      <div className="empty-state">
                        <h4>
                          No appointments
                        </h4>

                        <p>
                          There are no appointments
                          scheduled yet.
                        </p>
                      </div>

                    ) : (

                      <div className="appointment-list">

                        {appointments
                          .slice(0, 5)
                          .map(appointment => (
                            <AppointmentRow
                              key={appointment.id}
                              appointment={appointment}
                            />
                          ))}

                      </div>

                    )}

                  </div>

                </>
              )}

              {/* =================================================
                  APPOINTMENTS
                  ================================================= */}

              {page === 'appointments' && (
                <div>

                  <div className="section-header">

                    <div>
                      <p className="eyebrow">
                        Practice
                      </p>

                      <h3>
                        Appointments
                      </h3>

                      <p className="section-description">
                        Manage your upcoming patient
                        appointments and requests.
                      </p>
                    </div>

                    <div className="section-count">
                      {appointments.length}
                      {' '}
                      total
                    </div>

                  </div>

                  <div className="card appointments-card">

                    {appointments.length === 0 ? (

                      <div className="empty-state">
                        <h4>
                          No appointments found
                        </h4>

                        <p>
                          Your appointment schedule
                          is currently empty.
                        </p>
                      </div>

                    ) : (

                      <div className="appointment-list">

                        {appointments.map(
                          appointment => (
                            <AppointmentRow
                              key={appointment.id}
                              appointment={
                                appointment
                              }
                            />
                          )
                        )}

                      </div>

                    )}

                  </div>

                </div>
              )}

              {/* =================================================
                  PATIENTS
                  ================================================= */}

              {page === 'patients' && (
                <div className="patients-page">

                  <div className="section-header">

                    <div>
                      <p className="eyebrow">
                        Practice
                      </p>

                      <h3>
                        My Patients
                      </h3>

                      <p className="section-description">
                        View your patients and start a
                        conversation with them.
                      </p>
                    </div>

                    <div className="patients-count">
                      <strong>
                        {patients.length}
                      </strong>

                      <span>
                        {patients.length === 1
                          ? 'patient'
                          : 'patients'}
                      </span>
                    </div>

                  </div>

                  {patients.length === 0 ? (

                    <div className="card">

                      <div className="empty-state">

                        <h4>
                          No patients found
                        </h4>

                        <p>
                          Patients assigned to your
                          practice will appear here.
                        </p>

                      </div>

                    </div>

                  ) : (

                    <div className="patients-grid">

                      {patients.map(patient => (

                        <article
                          className="card patient-card"
                          key={patient.id}
                        >

                          <div className="patient-card-top">

                            <div className="patient-avatar">
                              {patient.firstName?.[0] ?? ''}
                              {patient.lastName?.[0] ?? ''}
                            </div>

                            <div className="patient-card-identity">

                              <h3>
                                {patient.firstName}{' '}
                                {patient.lastName}
                              </h3>

                              <span className="patient-status">
                                <i />
                                Active patient
                              </span>

                            </div>

                          </div>

                          <div className="patient-details">

                            <div className="patient-detail">

                              <span>
                                Phone
                              </span>

                              <strong>
                                {patient.phone ||
                                  'Not provided'}
                              </strong>

                            </div>

                            <div className="patient-detail">

                              <span>
                                Date of birth
                              </span>

                              <strong>
                                {patient.dateOfBirth ||
                                  'Not provided'}
                              </strong>

                            </div>

                          </div>

                          <div className="patient-card-footer">

                            <button
                              type="button"
                              className="btn btn-primary btn-sm patient-message-button"
                              onClick={() =>
                                openConversation(
                                  patient
                                )
                              }
                            >
                              <svg
                                viewBox="0 0 24 24"
                                aria-hidden="true"
                              >
                                <path d="M20 11.5a7.5 7.5 0 0 1-8 7.5 9 9 0 0 1-3-.5L4 20l1.5-3.5A7.5 7.5 0 1 1 20 11.5Z" />
                              </svg>

                              Message
                            </button>

                          </div>

                        </article>

                      ))}

                    </div>

                  )}

                </div>
              )}

              {/* =================================================
                  SCHEDULE
                  ================================================= */}

              {page === 'schedule' && (
                <div className="schedule-page">

                  <div className="section-header">

                    <div>
                      <p className="eyebrow">
                        Availability
                      </p>

                      <h3>
                        My Schedule
                      </h3>

                      <p className="section-description">
                        Your regular working hours and
                        availability.
                      </p>
                    </div>

                    <div className="schedule-count">

                      <strong>
                        {schedule.length}
                      </strong>

                      <span>
                        working days
                      </span>

                    </div>

                  </div>

                  {schedule.length === 0 ? (

                    <div className="card">

                      <div className="empty-state">

                        <h4>
                          No schedule configured
                        </h4>

                        <p>
                          Your working hours have not
                          been configured yet.
                        </p>

                      </div>

                    </div>

                  ) : (

                    <div className="card schedule-card">

                      <div className="schedule-card-header">

                        <div>
                          <strong>
                            Weekly availability
                          </strong>

                          <span>
                            Your regular working hours
                          </span>
                        </div>

                        <span className="schedule-header-status">
                          <i />
                          Active
                        </span>

                      </div>

                      <div className="schedule-list">

                        {schedule.map(item => (
                          <ScheduleRow
                            key={item.id}
                            item={item}
                          />
                        ))}

                      </div>

                    </div>

                  )}

                </div>
              )}

              {/* =================================================
                  MESSAGES
                  ================================================= */}

              {page === 'messages' && (
                <div className="messages-page">

                  <div className="section-header">

                    <div>
                      <p className="eyebrow">
                        Communication
                      </p>

                      <h3>
                        Messages
                      </h3>

                      <p className="section-description">
                        Communicate directly with your
                        patients.
                      </p>
                    </div>

                  </div>

                  <div className="card chat-card">

                    {/* PATIENT SIDEBAR */}

                    <div className="chat-sidebar">

                      <div className="chat-sidebar-header">

                        <div>
                          <strong>
                            Patients
                          </strong>

                          <span>
                            {patients.length}
                            {' '}
                            available
                          </span>
                        </div>

                      </div>

                      <div className="chat-patient-list">

                        {patients.length === 0 ? (

                          <div className="empty-state">
                            <p>
                              No patients found.
                            </p>
                          </div>

                        ) : (

                          patients.map(patient => {

                            const active =
                              selectedConversation
                                ? getConversationPatient(
                                    selectedConversation
                                  )?.id ===
                                  patient.id
                                : false;

                            return (
                              <button
                                key={patient.id}
                                type="button"
                                className={
                                  `chat-patient ${
                                    active
                                      ? 'chat-patient-active'
                                      : ''
                                  }`
                                }
                                onClick={() =>
                                  openConversation(
                                    patient
                                  )
                                }
                              >

                                <div className="avatar chat-avatar">
                                  {patient.firstName?.[0] ?? ''}
                                  {patient.lastName?.[0] ?? ''}
                                </div>

                                <div className="chat-patient-info">

                                  <strong>
                                    {patient.firstName}{' '}
                                    {patient.lastName}
                                  </strong>

                                  <span>
                                    {active
                                      ? 'Current conversation'
                                      : 'Start conversation'}
                                  </span>

                                </div>

                              </button>
                            );
                          })

                        )}

                      </div>

                    </div>

                    {/* CHAT */}

                    <div className="chat-main">

                      {!selectedConversation ? (

                        <div className="chat-empty">

                          <div className="chat-empty-icon">

                            <svg
                              viewBox="0 0 24 24"
                              aria-hidden="true"
                            >
                              <path d="M20 11.5a7.5 7.5 0 0 1-8 7.5 9 9 0 0 1-3-.5L4 20l1.5-3.5A7.5 7.5 0 1 1 20 11.5Z" />
                              <path d="M8 12h.01M12 12h.01M16 12h.01" />
                            </svg>

                          </div>

                          <h4>
                            Select a patient
                          </h4>

                          <p>
                            Choose a patient from the
                            list to start a conversation.
                          </p>

                        </div>

                      ) : (

                        <>

                          {/* CHAT HEADER */}

                          <div className="chat-header">

                            <div className="avatar chat-header-avatar">

                              {
                                getConversationPatient(
                                  selectedConversation
                                )?.firstName?.[0] ?? ''
                              }

                              {
                                getConversationPatient(
                                  selectedConversation
                                )?.lastName?.[0] ?? ''
                              }

                            </div>

                            <div className="chat-header-info">

                              <strong>
                                {
                                  getConversationPatientName(
                                    selectedConversation
                                  )
                                }
                              </strong>

                              <span>
                                Patient
                              </span>

                            </div>

                            <div className="chat-online">

                              <i />

                              Available

                            </div>

                          </div>

                          {/* MESSAGE AREA */}

                          <div className="chat-messages">

                            {messagesLoading ? (

                              <div className="chat-status">
                                <div className="chat-loading-dot" />
                                <span>
                                  Loading messages...
                                </span>
                              </div>

                            ) : messages.length === 0 ? (

                              <div className="chat-no-messages">

                                <div className="chat-no-messages-icon">

                                  <svg
                                    viewBox="0 0 24 24"
                                    aria-hidden="true"
                                  >
                                    <path d="M20 11.5a7.5 7.5 0 0 1-8 7.5 9 9 0 0 1-3-.5L4 20l1.5-3.5A7.5 7.5 0 1 1 20 11.5Z" />
                                  </svg>

                                </div>

                                <h4>
                                  No messages yet
                                </h4>

                                <p>
                                  Send a message to start
                                  the conversation.
                                </p>

                              </div>

                            ) : (

                              messages.map(message => {

                                const isMine =
                                  Number(
                                    message.senderId
                                  ) ===
                                  Number(
                                    dentist.userId
                                  );

                                return (
                                  <div
                                    key={message.id}
                                    className={
                                      `message-row ${
                                        isMine
                                          ? 'message-row-mine'
                                          : 'message-row-theirs'
                                      }`
                                    }
                                  >

                                    <div
                                      className={
                                        `message-bubble ${
                                          isMine
                                            ? 'message-bubble-mine'
                                            : 'message-bubble-theirs'
                                        }`
                                      }
                                    >

                                      <div className="message-content">
                                        {message.content}
                                      </div>

                                      <div className="message-time">
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

                          {/* COMPOSER */}

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
                                  event.key ===
                                    'Enter' &&
                                  !event.shiftKey
                                ) {
                                  event.preventDefault();
                                  sendMessage();
                                }

                              }}
                              disabled={
                                sendingMessage
                              }
                              className="chat-input"
                            />

                            <button
                              type="button"
                              className="btn btn-primary chat-send-button"
                              onClick={sendMessage}
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
                <div className="dentist-profile-page">

                  <div className="section-header">

                    <div>
                      <p className="eyebrow">
                        Professional profile
                      </p>

                      <h3>
                        My Profile
                      </h3>

                      <p className="section-description">
                        Your professional information
                        visible in the dental portal.
                      </p>
                    </div>

                  </div>

                  <div className="card dentist-profile-card">

                    {/* PROFILE HEADER */}

                    <div className="dentist-profile-header">

                      <div className="dentist-profile-avatar">

                        {dentist.firstName?.[0] ?? ''}
                        {dentist.lastName?.[0] ?? ''}

                      </div>

                      <div className="dentist-profile-identity">

                        <p className="dentist-profile-role">
                          Dental Professional
                        </p>

                        <h2>
                          Dr.{' '}
                          {dentist.firstName || ''}{' '}
                          {dentist.lastName || ''}
                        </h2>

                        <span>
                          {dentist.specialization ||
                            'General Dentistry'}
                        </span>

                      </div>

                    </div>

                    {/* INFORMATION */}

                    <div className="dentist-profile-section">

                      <div className="dentist-profile-section-title">
                        Professional information
                      </div>

                      <div className="dentist-profile-grid">

                        <div className="dentist-profile-field">

                          <span>
                            Specialization
                          </span>

                          <strong>
                            {dentist.specialization ||
                              'Not specified'}
                          </strong>

                        </div>

                        <div className="dentist-profile-field">

                          <span>
                            Clinic
                          </span>

                          <strong>
                            {dentist.clinicName ||
                              'Not specified'}
                          </strong>

                        </div>

                        <div className="dentist-profile-field">

                          <span>
                            Experience
                          </span>

                          <strong>
                            {dentist.experienceYears ??
                              0}
                            {' '}
                            years
                          </strong>

                        </div>

                        <div className="dentist-profile-field">

                          <span>
                            Phone
                          </span>

                          <strong>
                            {dentist.phone ||
                              'Not provided'}
                          </strong>

                        </div>

                      </div>

                    </div>

                    {/* BIOGRAPHY */}

                    <div className="dentist-profile-section">

                      <div className="dentist-profile-section-title">
                        About
                      </div>

                      <p className="dentist-profile-bio">
                        {dentist.biography ||
                          'No biography has been added yet.'}
                      </p>

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