import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8081",
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// REQUEST INTERCEPTOR — attach JWT
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwt');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// RESPONSE INTERCEPTOR
// Backend wraps all responses in:
// { success: true, message: "...", data: <payload> }
// This unwraps it automatically
axiosInstance.interceptors.response.use(
  (response) => {
    const res = response.data;
    if (
      res &&
      typeof res === 'object' &&
      'success' in res &&
      'data' in res
    ) {
      // Return unwrapped — 
      // callers get actual data directly
      return { 
        ...response, 
        data: res.data,
        message: res.message 
      };
    }
    return response;
  },
  (error) => {
    const status = error.response?.status;
    
    // Extract backend error message
    const backendMessage = 
      error.response?.data?.message ||
      error.response?.data?.error?.message ||
      error.message ||
      'Something went wrong';

    // Extract error details (validation etc)
    const errorDetails = 
      error.response?.data?.error?.details || 
      [];
    
    const errorCode = 
      error.response?.data?.error?.code || 
      'UNKNOWN_ERROR';

    // 401 — token expired → auto logout
    if (status === 401) {
      localStorage.removeItem('jwt');
      localStorage.removeItem('userRole');
      localStorage.removeItem('user');
      window.location.href = '/signin';
      return Promise.reject({
        message: 'Session expired. Please login again.',
        status: 401,
        code: 'UNAUTHORIZED'
      });
    }

    // Return structured error object
    return Promise.reject({
      message: backendMessage,
      status,
      code: errorCode,
      details: errorDetails
    });
  }
);

export default axiosInstance;
