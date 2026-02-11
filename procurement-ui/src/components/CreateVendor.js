import React, { useState } from 'react';
import {
  Container,
  Typography,
  TextField,
  Button,
  Grid,
  Card,
  CardContent,
  Box,
  AppBar,
  Toolbar,
  IconButton,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert
} from '@mui/material';
import { ArrowBack, Save } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

function CreateVendor() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [vendor, setVendor] = useState({
    name: '',
    email: '',
    phone: '',
    location: '',
    category: '',
    rating: 0,
    compliance: true
  });

  const handleChange = (field, value) => {
    setVendor(prev => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const token = localStorage.getItem('token');
      await axios.post('http://localhost:8080/api/vendors', null, {
        params: vendor,
        headers: { Authorization: `Bearer ${token}` }
      });
      setSuccess('Vendor created successfully!');
      setTimeout(() => navigate('/vendors'), 2000);
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to create vendor');
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <AppBar position="static">
        <Toolbar>
          <IconButton color="inherit" onClick={() => navigate('/vendors')}>
            <ArrowBack />
          </IconButton>
          <Typography variant="h6">
            Create New Vendor
          </Typography>
        </Toolbar>
      </AppBar>

      <Container maxWidth="md" sx={{ mt: 3 }}>
        <Card>
          <CardContent>
            <Typography variant="h5" gutterBottom>
              Vendor Information
            </Typography>

            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
            {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

            <Box component="form" onSubmit={handleSubmit}>
              <Grid container spacing={3}>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    required
                    label="Vendor Name"
                    value={vendor.name}
                    onChange={(e) => handleChange('name', e.target.value)}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    required
                    type="email"
                    label="Email"
                    value={vendor.email}
                    onChange={(e) => handleChange('email', e.target.value)}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    required
                    label="Phone"
                    value={vendor.phone}
                    onChange={(e) => {
                      const value = e.target.value.replace(/\D/g, '');
                      if (value.length <= 10) {
                        handleChange('phone', value);
                      }
                    }}
                    inputProps={{ maxLength: 10, pattern: '[0-9]{10}' }}
                    helperText="Enter 10 digit phone number"
                    error={vendor.phone.length > 0 && vendor.phone.length !== 10}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    required
                    label="Location"
                    value={vendor.location}
                    onChange={(e) => handleChange('location', e.target.value)}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <FormControl fullWidth required>
                    <InputLabel>Category</InputLabel>
                    <Select
                      value={vendor.category}
                      onChange={(e) => handleChange('category', e.target.value)}
                    >
                      <MenuItem value="Electronics">Electronics</MenuItem>
                      <MenuItem value="Office Supplies">Office Supplies</MenuItem>
                      <MenuItem value="Construction">Construction</MenuItem>
                      <MenuItem value="IT Services">IT Services</MenuItem>
                      <MenuItem value="Manufacturing">Manufacturing</MenuItem>
                      <MenuItem value="Logistics">Logistics</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    required
                    type="number"
                    label="Rating"
                    value={vendor.rating}
                    onChange={(e) => handleChange('rating', parseFloat(e.target.value))}
                    inputProps={{ min: 0, max: 5, step: 0.1 }}
                    helperText="Rating from 0 to 5"
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <FormControl fullWidth required>
                    <InputLabel>Compliance Status</InputLabel>
                    <Select
                      value={vendor.compliance}
                      onChange={(e) => handleChange('compliance', e.target.value)}
                    >
                      <MenuItem value={true}>Active (Compliant)</MenuItem>
                      <MenuItem value={false}>Inactive (Non-Compliant)</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>
              </Grid>

              <Box sx={{ mt: 3, display: 'flex', gap: 2 }}>
                <Button
                  type="submit"
                  variant="contained"
                  startIcon={<Save />}
                  disabled={loading}
                >
                  {loading ? 'Creating...' : 'Create Vendor'}
                </Button>
                <Button
                  variant="outlined"
                  onClick={() => navigate('/vendors')}
                >
                  Cancel
                </Button>
              </Box>
            </Box>
          </CardContent>
        </Card>
      </Container>
    </>
  );
}

export default CreateVendor;
