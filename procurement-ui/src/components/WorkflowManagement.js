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
  CircularProgress,
  Paper,
  Divider,
  Alert
} from '@mui/material';
import { ArrowBack, CheckCircle, Cancel, Assignment, AccessTime, Add, Delete, History } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { workflowAPI } from '../services/api';
import { formatCurrency } from '../utils/currency';

function WorkflowManagement() {
  const navigate = useNavigate();
  const [pendingRequisitions, setPendingRequisitions] = useState([]);
  const [selectedRequisition, setSelectedRequisition] = useState(null);
  const [openApprovalDialog, setOpenApprovalDialog] = useState(false);
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [requisitionToDelete, setRequisitionToDelete] = useState(null);
  const [openHistoryDialog, setOpenHistoryDialog] = useState(false);
  const [historyData, setHistoryData] = useState([]);
  const [loadingHistory, setLoadingHistory] = useState(false);
  const [approvalAction, setApprovalAction] = useState('');
  const [comment, setComment] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchPendingRequisitions = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await workflowAPI.getPending();
      const data = response.data;
      setPendingRequisitions(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Error fetching pending requisitions:', error);
      setError('Failed to load pending requisitions');
      setPendingRequisitions([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPendingRequisitions();
  }, []);

  const handleApprovalAction = async () => {
    try {
      if (approvalAction === 'approve') {
        await workflowAPI.approve(selectedRequisition.id, comment);
      } else {
        await workflowAPI.reject(selectedRequisition.id, comment);
      }
      setOpenApprovalDialog(false);
      setComment('');
      fetchPendingRequisitions();
    } catch (error) {
      console.error('Error processing approval:', error);
      setError('Failed to process approval');
    }
  };

  const openApproval = (requisition, action) => {
    setSelectedRequisition(requisition);
    setApprovalAction(action);
    setOpenApprovalDialog(true);
  };

  const handleDeleteRequisition = async () => {
    try {
      await workflowAPI.deleteRequisition(requisitionToDelete.id);
      setOpenDeleteDialog(false);
      setRequisitionToDelete(null);
      fetchPendingRequisitions();
    } catch (error) {
      console.error('Error deleting requisition:', error);
      setError('Failed to delete requisition');
    }
  };

  const fetchHistory = async (prId) => {
    setLoadingHistory(true);
    try {
      const response = await fetch(`http://localhost:8081/api/requisitions/${prId}/history`, {
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
      case 'DRAFT': return 'default';
      case 'SUBMITTED': return 'warning';
      case 'APPROVED': return 'success';
      case 'REJECTED': return 'error';
      default: return 'default';
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
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
          <Assignment sx={{ ml: 1, mr: 2, fontSize: 28 }} />
          <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 700 }}>
            Purchase Requisitions
          </Typography>
          <Button
            color="inherit"
            startIcon={<Add />}
            onClick={() => navigate('/create-requisition')}
            sx={{
              bgcolor: 'rgba(255,255,255,0.15)',
              '&:hover': { bgcolor: 'rgba(255,255,255,0.25)' },
              borderRadius: 2,
              textTransform: 'none',
              fontWeight: 600
            }}
          >
            Create PR
          </Button>
        </Toolbar>
      </AppBar>

      <Container maxWidth="xl" sx={{ mt: 3, mb: 4 }}>
        {error && (
          <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>
            {error}
          </Alert>
        )}

        <Box sx={{ mb: 3 }}>
          <Typography variant="h4" gutterBottom sx={{ fontWeight: 700, color: '#2d3748' }}>
            Pending Approvals
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 500 }}>
            {pendingRequisitions.length} requisitions awaiting approval
          </Typography>
        </Box>

        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
            <CircularProgress sx={{ color: '#667eea' }} />
          </Box>
        ) : pendingRequisitions.length === 0 ? (
          <Paper sx={{ p: 6, textAlign: 'center', borderRadius: 3, boxShadow: '0 4px 12px rgba(0,0,0,0.08)' }}>
            <AccessTime sx={{ fontSize: 64, color: '#667eea', mb: 2, opacity: 0.5 }} />
            <Typography variant="h6" sx={{ fontWeight: 600, color: '#2d3748' }}>
              No pending approvals
            </Typography>
            <Typography variant="body2" color="text.secondary">
              All requisitions have been processed
            </Typography>
          </Paper>
        ) : (
          <Grid container spacing={3}>
            {pendingRequisitions.map((requisition) => (
              <Grid item xs={12} md={6} key={requisition.id}>
                <Card sx={{ borderRadius: 3, boxShadow: '0 4px 12px rgba(0,0,0,0.08)', transition: 'all 0.3s', '&:hover': { transform: 'translateY(-4px)', boxShadow: '0 8px 24px rgba(102, 126, 234, 0.2)' } }}>
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                      <Typography variant="h6" sx={{ fontWeight: 600 }}>
                        PR #{requisition.prNumber}
                      </Typography>
                      <Chip 
                        label={requisition.status} 
                        color={getStatusColor(requisition.status)}
                        size="small"
                      />
                    </Box>

                    <Divider sx={{ my: 2 }} />

                    <Box sx={{ mb: 1 }}>
                      <Typography variant="body2" color="text.secondary">Description</Typography>
                      <Typography variant="body1">{requisition.description}</Typography>
                    </Box>
                    
                    <Grid container spacing={2} sx={{ my: 1 }}>
                      <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Quantity</Typography>
                        <Typography variant="body1">{requisition.quantity}</Typography>
                      </Grid>
                      <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Total Amount</Typography>
                        <Typography variant="body1" color="primary" sx={{ fontWeight: 600 }}>
                          {formatCurrency(requisition.totalAmount)}
                        </Typography>
                      </Grid>
                    </Grid>

                    <Typography variant="caption" color="text.secondary" display="block" sx={{ mt: 2 }}>
                      Requested: {formatDate(requisition.createdAt)}
                    </Typography>

                    <Box sx={{ mt: 3, display: 'flex', gap: 1 }}>
                      <Button
                        variant="contained"
                        color="success"
                        size="small"
                        startIcon={<CheckCircle />}
                        onClick={() => openApproval(requisition, 'approve')}
                      >
                        Approve
                      </Button>
                      <Button
                        variant="outlined"
                        color="error"
                        size="small"
                        startIcon={<Cancel />}
                        onClick={() => openApproval(requisition, 'reject')}
                      >
                        Reject
                      </Button>
                      <Button
                        variant="outlined"
                        color="primary"
                        size="small"
                        startIcon={<History />}
                        onClick={() => fetchHistory(requisition.id)}
                      >
                        History
                      </Button>
                      <Button
                        variant="outlined"
                        color="error"
                        size="small"
                        startIcon={<Delete />}
                        onClick={() => {
                          setRequisitionToDelete(requisition);
                          setOpenDeleteDialog(true);
                        }}
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

        <Dialog open={openApprovalDialog} onClose={() => setOpenApprovalDialog(false)} maxWidth="sm" fullWidth>
          <DialogTitle>
            {approvalAction === 'approve' ? 'Approve' : 'Reject'} Requisition
          </DialogTitle>
          <DialogContent>
            <Box sx={{ mb: 2 }}>
              <Typography variant="body1" gutterBottom>
                PR #{selectedRequisition?.prNumber}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Amount: {formatCurrency(selectedRequisition?.totalAmount)}
              </Typography>
            </Box>
            <TextField
              fullWidth
              label="Comments"
              multiline
              rows={4}
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              margin="normal"
              placeholder="Add your comments here..."
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenApprovalDialog(false)}>Cancel</Button>
            <Button 
              onClick={handleApprovalAction} 
              variant="contained"
              color={approvalAction === 'approve' ? 'success' : 'error'}
            >
              {approvalAction === 'approve' ? 'Approve' : 'Reject'}
            </Button>
          </DialogActions>
        </Dialog>

        <Dialog open={openDeleteDialog} onClose={() => setOpenDeleteDialog(false)}>
          <DialogTitle>Delete Purchase Requisition</DialogTitle>
          <DialogContent>
            <Typography>
              Are you sure you want to delete PR #{requisitionToDelete?.prNumber}? This action cannot be undone.
            </Typography>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenDeleteDialog(false)}>Cancel</Button>
            <Button onClick={handleDeleteRequisition} variant="contained" color="error">Delete</Button>
          </DialogActions>
        </Dialog>

        <Dialog open={openHistoryDialog} onClose={() => setOpenHistoryDialog(false)} maxWidth="md" fullWidth>
          <DialogTitle>Purchase Requisition History</DialogTitle>
          <DialogContent>
            {loadingHistory ? (
              <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
                <CircularProgress />
              </Box>
            ) : historyData.length === 0 ? (
              <Typography color="text.secondary">No history available</Typography>
            ) : (
              <Box sx={{ mt: 2 }}>
                {historyData.map((entry, index) => (
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

export default WorkflowManagement;
