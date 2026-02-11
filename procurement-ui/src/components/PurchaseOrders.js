import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Button,
  Grid,
  Card,
  CardContent,
  Box,
  Chip,
  AppBar,
  Toolbar,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  CircularProgress,
  Divider,
  Alert,
  Autocomplete
} from '@mui/material';
import { ArrowBack, Add, ShoppingCart, Receipt, Delete, History } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { purchaseOrderAPI, vendorAPI } from '../services/api';
import { formatCurrency } from '../utils/currency';

function PurchaseOrders() {
  const navigate = useNavigate();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [orderToDelete, setOrderToDelete] = useState(null);
  const [openHistoryDialog, setOpenHistoryDialog] = useState(false);
  const [historyData, setHistoryData] = useState([]);
  const [loadingHistory, setLoadingHistory] = useState(false);
  const [openCreateDialog, setOpenCreateDialog] = useState(false);
  const [newOrder, setNewOrder] = useState({ title: '', vendorId: null });
  const [activeVendors, setActiveVendors] = useState([]);
  const [newItem, setNewItem] = useState({
    itemName: '',
    quantity: 1,
    unitPrice: 0,
    tax: 0,
    discount: 0
  });

  const fetchOrders = async () => {
    setLoading(true);
    try {
      const response = await purchaseOrderAPI.getAll();
      setOrders(response.data.content || []);
    } catch (error) {
      console.error('Error fetching orders:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
    fetchActiveVendors();
  }, []);

  const fetchActiveVendors = async () => {
    try {
      const response = await vendorAPI.getActive();
      setActiveVendors(response.data);
    } catch (error) {
      console.error('Error fetching active vendors:', error);
    }
  };

  const createNewOrder = async () => {
    setError('');
    if (!newOrder.vendorId) {
      setError('Please select a vendor');
      return;
    }
    try {
      await purchaseOrderAPI.create(newOrder.title, newOrder.vendorId);
      setOpenCreateDialog(false);
      setNewOrder({ title: '', vendorId: null });
      fetchOrders();
    } catch (error) {
      console.error('Error creating order:', error);
      setError(error.response?.data?.message || 'Failed to create order.');
    }
  };

  const addItemToOrder = async () => {
    try {
      await purchaseOrderAPI.addItem(selectedOrder.id, newItem);
      setOpenDialog(false);
      setNewItem({ itemName: '', quantity: 1, unitPrice: 0, tax: 0, discount: 0 });
      fetchOrders();
    } catch (error) {
      console.error('Error adding item:', error);
    }
  };

  const updateItemStatus = async (itemId, status) => {
    try {
      await purchaseOrderAPI.updateItemStatus(itemId, status);
      fetchOrders();
    } catch (error) {
      console.error('Error updating item status:', error);
    }
  };

  const handleDeleteOrder = async () => {
    try {
      await purchaseOrderAPI.delete(orderToDelete.id);
      setOpenDeleteDialog(false);
      setOrderToDelete(null);
      fetchOrders();
    } catch (error) {
      console.error('Error deleting order:', error);
      setError('Failed to delete order');
    }
  };

  const fetchHistory = async (poId) => {
    setLoadingHistory(true);
    try {
      const response = await fetch(`http://localhost:8081/api/purchase-orders/${poId}/history`, {
        headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
      });
      const data = await response.json();
      setHistoryData(data);
      setOpenHistoryDialog(true);
    } catch (error) {
      console.error('Error fetching history:', error);
      setError('Failed to load history');
    } finally {
      setLoadingHistory(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'OPEN': return 'primary';
      case 'CLOSED': return 'success';
      case 'CANCELLED': return 'error';
      default: return 'default';
    }
  };

  const getItemStatusColor = (status) => {
    switch (status) {
      case 'PENDING': return 'warning';
      case 'PARTIALLY_DELIVERED': return 'info';
      case 'DELIVERED': return 'success';
      default: return 'default';
    }
  };

  return (
    <Box sx={{ minHeight: '100vh', bgcolor: '#f8f9fa' }}>
      <AppBar 
        position="static" 
        elevation={0}
        sx={{
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
        }}
      >
        <Toolbar sx={{ py: 1 }}>
          <IconButton color="inherit" onClick={() => navigate('/')} edge="start">
            <ArrowBack />
          </IconButton>
          <ShoppingCart sx={{ ml: 1, mr: 2, fontSize: 28 }} />
          <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 700 }}>
            Purchase Orders
          </Typography>
          <Button 
            color="inherit" 
            startIcon={<Add />} 
            onClick={() => setOpenCreateDialog(true)}
            sx={{
              bgcolor: 'rgba(255,255,255,0.15)',
              '&:hover': { bgcolor: 'rgba(255,255,255,0.25)' },
              borderRadius: 2,
              textTransform: 'none',
              fontWeight: 600
            }}
          >
            New Order
          </Button>
        </Toolbar>
      </AppBar>

      <Container maxWidth="xl" sx={{ mt: 3, mb: 4 }}>
        {error && (
          <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }} onClose={() => setError('')}>
            {error}
          </Alert>
        )}

        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
            <CircularProgress sx={{ color: '#667eea' }} />
          </Box>
        ) : (
          <Grid container spacing={3}>
            {orders.map((order) => (
              <Grid item xs={12} key={order.id}>
                <Card sx={{ borderRadius: 3, boxShadow: '0 4px 12px rgba(0,0,0,0.08)' }}>
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <Box sx={{
                          width: 40,
                          height: 40,
                          borderRadius: 2,
                          background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          mr: 1.5
                        }}>
                          <Receipt sx={{ color: 'white', fontSize: 20 }} />
                        </Box>
                        <Typography variant="h6" sx={{ fontWeight: 700, color: '#2d3748' }}>
                          {order.title} <Typography component="span" color="text.secondary">#{order.id}</Typography>
                        </Typography>
                      </Box>
                      <Chip 
                        label={order.status} 
                        color={getStatusColor(order.status)}
                      />
                    </Box>

                    <Grid container spacing={2} sx={{ mb: 2 }}>
                      <Grid item xs={3}>
                        <Typography variant="body2" color="text.secondary">Subtotal</Typography>
                        <Typography variant="h6">{formatCurrency(order.subtotal || 0)}</Typography>
                      </Grid>
                      <Grid item xs={3}>
                        <Typography variant="body2" color="text.secondary">Tax</Typography>
                        <Typography variant="h6">{formatCurrency(order.tax || 0)}</Typography>
                      </Grid>
                      <Grid item xs={3}>
                        <Typography variant="body2" color="text.secondary">Discount</Typography>
                        <Typography variant="h6">{formatCurrency(order.discount || 0)}</Typography>
                      </Grid>
                      <Grid item xs={3}>
                        <Typography variant="body2" color="text.secondary">Total</Typography>
                        <Typography variant="h6" color="primary">{formatCurrency(order.totalAmount || 0)}</Typography>
                      </Grid>
                    </Grid>

                    <Divider sx={{ my: 2 }} />

                    {order.items && order.items.length > 0 ? (
                      <TableContainer component={Paper} variant="outlined">
                        <Table size="small">
                          <TableHead>
                            <TableRow>
                              <TableCell sx={{ fontWeight: 600 }}>Item</TableCell>
                              <TableCell sx={{ fontWeight: 600 }}>Qty</TableCell>
                              <TableCell sx={{ fontWeight: 600 }}>Unit Price (INR)</TableCell>
                              <TableCell sx={{ fontWeight: 600 }}>Total (INR)</TableCell>
                              <TableCell sx={{ fontWeight: 600 }}>Status</TableCell>
                              <TableCell sx={{ fontWeight: 600 }}>Actions</TableCell>
                            </TableRow>
                          </TableHead>
                          <TableBody>
                            {order.items.map((item) => (
                              <TableRow key={item.id}>
                                <TableCell>{item.itemName}</TableCell>
                                <TableCell>{item.quantity}</TableCell>
                                <TableCell>{formatCurrency(item.unitPrice)}</TableCell>
                                <TableCell>{formatCurrency(item.itemTotal)}</TableCell>
                                <TableCell>
                                  <Chip 
                                    label={item.status} 
                                    size="small"
                                    color={getItemStatusColor(item.status)}
                                  />
                                </TableCell>
                                <TableCell>
                                  {item.status !== 'DELIVERED' && (
                                    <FormControl size="small" sx={{ minWidth: 150 }}>
                                      <Select
                                        value={item.status}
                                        onChange={(e) => updateItemStatus(item.id, e.target.value)}
                                      >
                                        <MenuItem value="PENDING">Pending</MenuItem>
                                        <MenuItem value="PARTIALLY_DELIVERED">Partially Delivered</MenuItem>
                                        <MenuItem value="DELIVERED">Delivered</MenuItem>
                                      </Select>
                                    </FormControl>
                                  )}
                                </TableCell>
                              </TableRow>
                            ))}
                          </TableBody>
                        </Table>
                      </TableContainer>
                    ) : (
                      <Typography variant="body2" color="text.secondary" sx={{ textAlign: 'center', py: 3 }}>
                        No items in this order
                      </Typography>
                    )}

                    <Box sx={{ mt: 2 }}>
                      <Button
                        variant="outlined"
                        startIcon={<Add />}
                        onClick={() => {
                          setSelectedOrder(order);
                          setOpenDialog(true);
                        }}
                        disabled={order.status === 'CLOSED'}
                      >
                        Add Item
                      </Button>
                      <Button
                        variant="outlined"
                        color="primary"
                        startIcon={<History />}
                        onClick={() => fetchHistory(order.id)}
                        sx={{ ml: 1 }}
                      >
                        History
                      </Button>
                      <Button
                        variant="outlined"
                        color="error"
                        startIcon={<Delete />}
                        onClick={() => {
                          setOrderToDelete(order);
                          setOpenDeleteDialog(true);
                        }}
                        sx={{ ml: 1 }}
                      >
                        Delete
                      </Button>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        )}

        <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
          <DialogTitle>Add Item to Order</DialogTitle>
          <DialogContent>
            <TextField
              fullWidth
              label="Item Name"
              value={newItem.itemName}
              onChange={(e) => setNewItem({ ...newItem, itemName: e.target.value })}
              margin="normal"
            />
            <TextField
              fullWidth
              label="Quantity"
              type="number"
              value={newItem.quantity}
              onChange={(e) => setNewItem({ ...newItem, quantity: parseInt(e.target.value) })}
              margin="normal"
            />
            <TextField
              fullWidth
              label="Unit Price (INR)"
              type="number"
              value={newItem.unitPrice}
              onChange={(e) => setNewItem({ ...newItem, unitPrice: parseFloat(e.target.value) })}
              margin="normal"
            />
            <TextField
              fullWidth
              label="Tax (INR)"
              type="number"
              value={newItem.tax}
              onChange={(e) => setNewItem({ ...newItem, tax: parseFloat(e.target.value) })}
              margin="normal"
            />
            <TextField
              fullWidth
              label="Discount (INR)"
              type="number"
              value={newItem.discount}
              onChange={(e) => setNewItem({ ...newItem, discount: parseFloat(e.target.value) })}
              margin="normal"
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
            <Button onClick={addItemToOrder} variant="contained">Add Item</Button>
          </DialogActions>
        </Dialog>

        <Dialog open={openDeleteDialog} onClose={() => setOpenDeleteDialog(false)}>
          <DialogTitle>Delete Purchase Order</DialogTitle>
          <DialogContent>
            <Typography>
              Are you sure you want to delete {orderToDelete?.title}? This action cannot be undone.
            </Typography>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenDeleteDialog(false)}>Cancel</Button>
            <Button onClick={handleDeleteOrder} variant="contained" color="error">Delete</Button>
          </DialogActions>
        </Dialog>

        <Dialog open={openCreateDialog} onClose={() => setOpenCreateDialog(false)} maxWidth="sm" fullWidth>
          <DialogTitle>Create Purchase Order</DialogTitle>
          <DialogContent>
            <TextField
              fullWidth
              label="Title"
              value={newOrder.title}
              onChange={(e) => setNewOrder({ ...newOrder, title: e.target.value })}
              margin="normal"
            />
            <Autocomplete
              options={activeVendors}
              getOptionLabel={(option) => `${option.vendorName} (Rating: ${option.rating || 'N/A'})`}
              value={activeVendors.find(v => v.vendorId === newOrder.vendorId) || null}
              onChange={(e, newValue) => setNewOrder({ ...newOrder, vendorId: newValue?.vendorId || null })}
              renderInput={(params) => (
                <TextField
                  {...params}
                  label="Select Active Vendor"
                  margin="normal"
                  required
                  helperText={activeVendors.length === 0 ? 'No active vendors available' : 'Select a vendor from the list'}
                />
              )}
              renderOption={(props, option) => (
                <li {...props}>
                  <Box>
                    <Typography variant="body1">{option.vendorName}</Typography>
                    <Typography variant="caption" color="text.secondary">
                      {option.category} | {option.location} | Rating: {option.rating || 'N/A'}
                    </Typography>
                  </Box>
                </li>
              )}
              disabled={activeVendors.length === 0}
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenCreateDialog(false)}>Cancel</Button>
            <Button onClick={createNewOrder} variant="contained" disabled={!newOrder.vendorId}>Create</Button>
          </DialogActions>
        </Dialog>

        <Dialog open={openHistoryDialog} onClose={() => setOpenHistoryDialog(false)} maxWidth="md" fullWidth>
          <DialogTitle>Purchase Order History</DialogTitle>
          <DialogContent>
            {loadingHistory ? (
              <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
                <CircularProgress />
              </Box>
            ) : historyData.length === 0 ? (
              <Typography color="text.secondary">No history available</Typography>
            ) : (
              <Box sx={{ mt: 2 }}>
                {historyData.map((entry) => (
                  <Paper key={entry.id} sx={{ p: 2, mb: 2, bgcolor: '#f8f9fa', borderRadius: 2 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                      <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>{entry.action}</Typography>
                      <Chip label={entry.status} size="small" color="primary" />
                    </Box>
                    {entry.remarks && (
                      <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                        {entry.remarks}
                      </Typography>
                    )}
                    <Typography variant="caption" color="text.secondary">
                      {new Date(entry.createdAt).toLocaleString()}
                    </Typography>
                  </Paper>
                ))}
              </Box>
            )}
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenHistoryDialog(false)}>Close</Button>
          </DialogActions>
        </Dialog>
      </Container>
    </Box>
  );
}

export default PurchaseOrders;
