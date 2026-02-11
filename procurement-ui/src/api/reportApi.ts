import axiosInstance from './axiosInstance';

export const reportApi = {
  vendor: (format = 'pdf') => 
    axiosInstance.get('/reports/vendor', { params: { format }, responseType: 'blob' }),
  
  pr: (format = 'pdf') => 
    axiosInstance.get('/reports/pr', { params: { format }, responseType: 'blob' }),
  
  po: (format = 'pdf') => 
    axiosInstance.get('/reports/po', { params: { format }, responseType: 'blob' }),
};
