import React, { useState } from 'react';
import {
  Container,
  Typography,
  TextField,
  Button,
  Card,
  CardContent,
  Box,
  AppBar,
  Toolbar,
  IconButton,
  Alert,
  Grid,
  CircularProgress
} from '@mui/material';
import { ArrowBack, Save, Assignment } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

function CreateRequisition() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [requisition, setRequisition] = useState({
    requisitionNumber: '',
    description: '',
    quantity: 1,
    totalAmount: 0
  });

  const handleChange = (field, value) => {
    setRequisition(prev => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const token = localStorage.getItem('token');
      await axios.post('http://localhost:8080/api/requisitions', null, {
        params: requisition,
        headers: { Authorization: `Bearer ${token}` }
      });
      setSuccess('Purchase Requisition created successfully!');
      setTimeout(() => navigate('/workflow'), 2000);
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to create requisition');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
      <AppBar position="static" elevation={0}>
        <Toolbar>
          <IconButton color="inherit" onClick={() => navigate('/workflow')} edge="start">
            <ArrowBack />
          </IconButton>
          <Assignment sx={{ ml: 1, mr: 2 }} />
          <Typography variant="h6" sx={{ fontWeight: 600 }}>
            Create Purchase Requisition
          </Typography>
        </Toolbar>
      </AppBar>

      <Container maxWidth="md" sx={{ mt: 3, mb: 4 }}>
        <Card>
          <CardContent>
            <Typography variant="h5" gutterBottom sx={{ mb: 3 }}>
              Requisition Details
            </Typography>

            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
            {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

            <Box component="form" onSubmit={handleSubmit}>
              <Grid container spacing={3}>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    required
                    label="Requisition Number"
                    value={requisition.requisitionNumber}
                    onChange={(e) => handleChange('requisitionNumber', e.target.value)}
                    placeholder="e.g., REQ004"
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    required
                    label="Description"
                    multiline
                    rows={3}
                    value={requisition.description}
                    onChange={(e) => handleChange('description', e.target.value)}
                    placeholder="Describe the items or services needed"
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    required
                    type="number"
                    label="Quantity"
                    value={requisition.quantity}
                    onChange={(e) => handleChange('quantity', parseInt(e.target.value))}
                    inputProps={{ min: 1 }}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    required
                    type="number"
                    label="Total Amount (INR)"
                    value={requisition.totalAmount}
                    onChange={(e) => handleChange('totalAmount', parseFloat(e.target.value))}
                    inputProps={{ min: 0, step: 0.01 }}
                  />
                </Grid>
              </Grid>

              <Box sx={{ mt: 3, display: 'flex', gap: 2 }}>
                <Button
                  type="submit"
                  variant="contained"
                  startIcon={loading ? <CircularProgress size={20} /> : <Save />}
                  disabled={loading}
                >
                  {loading ? 'Creating...' : 'Create Requisition'}
                </Button>
                <Button
                  variant="outlined"
                  onClick={() => navigate('/workflow')}
                >
                  Cancel
                </Button>
              </Box>
            </Box>
          </CardContent>
        </Card>
      </Container>
    </Box>
  );
}

export default CreateRequisition;
