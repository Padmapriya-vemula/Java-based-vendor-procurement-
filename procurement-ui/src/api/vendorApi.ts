import axiosInstance from './axiosInstance';

export const vendorApi = {
  search: (params: any) => 
    axiosInstance.get('/vendors/search', { params }),
  
  getAll: (page = 0, size = 10) => 
    axiosInstance.get('/vendors/search', { params: { page, size } }),
};
