import api from './api';

export const dashboardAPI = {
  getOverview: () => api.get('/dashboard/overview'),
  getPRStatus: () => api.get('/dashboard/pr-status'),
  getPOStatus: () => api.get('/dashboard/po-status'),
  getMonthlyTrend: () => api.get('/dashboard/monthly-trend'),
  getVendorPOValues: () => api.get('/dashboard/vendor-po-values'),
};
