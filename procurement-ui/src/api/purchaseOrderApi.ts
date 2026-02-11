import axiosInstance from './axiosInstance';

export const purchaseOrderApi = {
  getAll: (page = 0, size = 10) => 
    axiosInstance.get('/purchase-orders', { params: { page, size } }),
  
  create: (title: string) => 
    axiosInstance.post('/purchase-orders', null, { params: { title } }),
  
  addItem: (poId: number, itemName: string, quantity: number, unitPrice: number, tax?: number, discount?: number) => 
    axiosInstance.post(`/purchase-orders/${poId}/items`, null, { 
      params: { itemName, quantity, unitPrice, tax, discount } 
    }),
  
  updateItem: (itemId: number, quantity?: number, unitPrice?: number, tax?: number, discount?: number) => 
    axiosInstance.put(`/purchase-orders/items/${itemId}`, null, { 
      params: { quantity, unitPrice, tax, discount } 
    }),
  
  updateItemStatus: (itemId: number, status: string) => 
    axiosInstance.put(`/purchase-orders/items/${itemId}/status`, null, { params: { status } }),
};
