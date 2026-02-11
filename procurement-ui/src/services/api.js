import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle token expiration
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
};

export const statsAPI = {
  get: () => api.get('/stats'),
};

export const vendorAPI = {
  search: (params) => api.get('/vendors/search', { params }),
  getActive: () => api.get('/vendors/active'),
  create: (vendor) => api.post('/vendors', vendor),
  update: (id, vendor) => api.put(`/vendors/${id}`, vendor),
  delete: (id) => api.delete(`/vendors/${id}`),
};

export const purchaseOrderAPI = {
  getAll: (params) => api.get('/purchase-orders', { params }),
  create: (title, vendorId) => api.post('/purchase-orders', null, { params: { title, vendorId } }),
  addItem: (poId, item) => api.post(`/purchase-orders/${poId}/items`, null, { params: item }),
  updateItemStatus: (itemId, status) => api.put(`/purchase-orders/items/${itemId}/status`, null, { params: { status } }),
  delete: (id) => api.delete(`/purchase-orders/${id}`),
};

export const workflowAPI = {
  submit: (id) => api.post(`/workflow/requisitions/${id}/submit`),
  approve: (id, comment) => api.post(`/workflow/requisitions/${id}/approve`, { comment }),
  reject: (id, comment) => api.post(`/workflow/requisitions/${id}/reject`, { comment }),
  getHistory: (id) => api.get(`/workflow/requisitions/${id}/history`),
  getPending: () => api.get('/workflow/requisitions/pending'),
  deleteRequisition: (id) => api.delete(`/requisitions/${id}`),
};

export const reportAPI = {
  vendor: (params) => api.get('/reports/vendor', { params, responseType: 'blob' }),
  pr: (params) => api.get('/reports/pr', { params, responseType: 'blob' }),
  po: (params) => api.get('/reports/po', { params, responseType: 'blob' }),
};

export const dashboardAPI = {
  getOverview: () => api.get('/dashboard/overview'),
  getPRStatus: () => api.get('/dashboard/pr-status'),
  getPOStatus: () => api.get('/dashboard/po-status'),
  getMonthlyTrend: () => api.get('/dashboard/monthly-trend'),
  getVendorPOValues: () => api.get('/dashboard/vendor-po-values'),
};

export default api;