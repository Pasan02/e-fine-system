import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
});

apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('adminToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export const authService = {
  login: async (username, password) => {
    try {
      const response = await axios.post(`${API_BASE_URL}/auth/login`, {
        username,
        password,
      });
      // The backend returns { accessToken, role, ... }
      return response.data;
    } catch (error) {
      if (error.response && error.response.data) {
        throw new Error(error.response.data.message || "Login failed", { cause: error });
      }
      throw new Error("Invalid username or password", { cause: error });
    }
  }
};

export const adminService = {
  getCollectionsSummary: async () => {
    const response = await apiClient.get('/admin/dashboard');
    return response.data;
  },

  getDistrictCollections: async () => {
    const response = await apiClient.get('/admin/reports/districts');
    return response.data;
  },

  getCategoryCollections: async () => {
    const response = await apiClient.get('/admin/reports/categories');
    return response.data;
  },

  getCollectionTrends: async () => {
    // If backend does not support trend natively, fetch payments and aggregate
    // Or we provide a fallback empty list if not implemented in backend yet.
    try {
      const response = await apiClient.get('/admin/payments');
      const payments = response.data || [];
      
      const trendsMap = {};
      payments.forEach(p => {
        const date = p.paidAt ? p.paidAt.split('T')[0] : 'Unknown';
        if (!trendsMap[date]) {
          trendsMap[date] = { date, finesCount: 0, revenue: 0 };
        }
        trendsMap[date].finesCount += 1;
        trendsMap[date].revenue += p.amountPaid || 0;
      });

      return Object.values(trendsMap).sort((a, b) => new Date(a.date) - new Date(b.date));
    } catch {
      return [];
    }
  },

  getRecentFines: async () => {
    const response = await apiClient.get('/admin/fines');
    return response.data;
  }
};
