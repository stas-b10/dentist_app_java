const API_BASE_URL = '/api';

export class ApiError extends Error {
  status: number;

  constructor(message: string, status: number) {
    super(message);
    this.status = status;
  }
}

async function request<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<T> {
  const token = localStorage.getItem('token');

  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',

      ...(token
        ? {
            Authorization: `Bearer ${token}`,
          }
        : {}),

      ...(options.headers || {}),
    },
  });

  if (!response.ok) {
    let message = `Request failed with status ${response.status}`;

    try {
      const data = await response.json();

      if (data.message) {
        message = data.message;
      } else if (data.error) {
        message = data.error;
      }
    } catch {
      // Response wasn't JSON.
    }

    throw new ApiError(message, response.status);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json();
}

export const api = {
  get<T>(endpoint: string) {
    return request<T>(endpoint);
  },

  post<T>(endpoint: string, body?: unknown) {
    return request<T>(endpoint, {
      method: 'POST',
      body:
        body !== undefined
          ? JSON.stringify(body)
          : undefined,
    });
  },

  put<T>(endpoint: string, body?: unknown) {
    return request<T>(endpoint, {
      method: 'PUT',
      body:
        body !== undefined
          ? JSON.stringify(body)
          : undefined,
    });
  },

  patch<T>(endpoint: string, body?: unknown) {
    return request<T>(endpoint, {
      method: 'PATCH',
      body:
        body !== undefined
          ? JSON.stringify(body)
          : undefined,
    });
  },

  delete<T>(endpoint: string) {
    return request<T>(endpoint, {
      method: 'DELETE',
    });
  },
};