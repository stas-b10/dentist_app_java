import type {
  Client,
  Dentist,
  Treatment,
} from '../types';

/**
 * Get client's full name.
 */
export function clientName(
  clients: Client[],
  clientId: string
): string {
  const client = clients.find(
    (item) => String(item.id) === String(clientId)
  );

  if (!client) {
    return 'Unknown patient';
  }

  const name =
    `${client.firstName ?? ''} ${client.lastName ?? ''}`.trim();

  return name || 'Unknown patient';
}

/**
 * Get dentist's full name.
 */
export function dentistName(
  dentists: Dentist[],
  dentistId: string
): string {
  const dentist = dentists.find(
    (item) => String(item.id) === String(dentistId)
  );

  if (!dentist) {
    return 'Unknown dentist';
  }

  const name =
    `${dentist.firstName ?? ''} ${dentist.lastName ?? ''}`.trim();

  if (!name) {
    return 'Unknown dentist';
  }

  return `Dr. ${name}`;
}

/**
 * Get treatment name.
 */
export function treatmentName(
  treatments: Treatment[],
  treatmentId: string
): string {
  const treatment = treatments.find(
    (item) =>
      String(item.id) === String(treatmentId)
  );

  if (!treatment) {
    return 'Unknown treatment';
  }

  return treatment.name ?? 'Unknown treatment';
}

/**
 * Format date safely.
 */
export function formatDate(
  value?: string | null
): string {
  if (!value) {
    return 'No date';
  }

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  });
}

/**
 * Format LocalTime returned by Spring.
 *
 * Example:
 * 10:00:00 -> 10:00
 * 14:30:00 -> 14:30
 */
export function formatTime(
  value?: string | null
): string {
  if (!value) {
    return '--:--';
  }

  return value.slice(0, 5);
}