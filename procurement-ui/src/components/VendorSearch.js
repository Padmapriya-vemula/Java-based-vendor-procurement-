import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  TextField,
  Button,
  Grid,
  Card,
  CardContent,
  Box,
  Chip,
  Rating,
  Pagination,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  AppBar,
  Toolbar,
  IconButton,
  CircularProgress,
  Paper,
  Divider
} from '@mui/material';
import { ArrowBack, Search, Add, Business, Phone, Email, LocationOn } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { vendorAPI } from '../services/api';

function VendorSearch() {
  const navigate = useNavigate();
  const [vendors, setVendors] = useState([]);
  const [loading, setLoading] = useState(false);
  const [filters, setFilters] = useState({
    rating: '',
    location: '',
    category: '',
    compliance: ''
  });
  const [pagination, setPagination] = useState({
    page: 0,
    size: 12,
    totalPages: 0,
    totalElements: 0
  });

  const searchVendors = async (page = 0) => {
    setLoading(true);
    try {
      const params = {
        ...filters,
        page,
        size: pagination.size,
        sort: 'rating,desc'
      };
      
      Object.keys(params).forEach(key => {
        if (params[key] === '') delete params[key];
      });

      const response = await vendorAPI.search(params);
      setVendors(response.data.content || []);
      setPagination(prev => ({
        ...prev,
        page: response.data.number || 0,
        totalPages: response.data.totalPages || 0,
        totalElements: response.data.totalElements || 0
      }));
    } catch (error) {
      console.error('Error searching vendors:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    searchVendors();
  }, []);

  const handleFilterChange = (field, value) => {
    setFilters(prev => ({ ...prev, [field]: value }));
  };

  const handleSearch = () => {
    searchVendors(0);
  };

  const handlePageChange = (event, newPage) => {
    searchVendors(newPage - 1);
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
          <Business sx={{ ml: 1, mr: 2, fontSize: 28 }} />
          <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 700 }}>
            Vendor Management
          </Typography>
          <Button
            color="inherit"
            startIcon={<Add />}
            onClick={() => navigate('/create-vendor')}
            sx={{
              bgcolor: 'rgba(255,255,255,0.15)',
              '&:hover': { bgcolor: 'rgba(255,255,255,0.25)' },
              borderRadius: 2,
              textTransform: 'none',
              fontWeight: 600
            }}
          >
            Add Vendor
          </Button>
        </Toolbar>
      </AppBar>

      <Container maxWidth="xl" sx={{ mt: 3, mb: 4 }}>
        <Paper sx={{ p: 3, mb: 3, borderRadius: 3, boxShadow: '0 4px 12px rgba(0,0,0,0.08)' }}>
          <Typography variant="h6" gutterBottom sx={{ mb: 2, fontWeight: 700, color: '#2d3748' }}>
            Search Filters
          </Typography>
          <Grid container spacing={2} alignItems="center">
            <Grid item xs={12} sm={6} md={2}>
              <TextField
                fullWidth
                label="Min Rating"
                type="number"
                size="small"
                inputProps={{ min: 0, max: 5, step: 0.1 }}
                value={filters.rating}
                onChange={(e) => handleFilterChange('rating', e.target.value)}
              />
            </Grid>
            <Grid item xs={12} sm={6} md={2}>
              <TextField
                fullWidth
                label="Location"
                size="small"
                value={filters.location}
                onChange={(e) => handleFilterChange('location', e.target.value)}
              />
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <FormControl fullWidth size="small">
                <InputLabel>Category</InputLabel>
                <Select
                  value={filters.category}
                  label="Category"
                  onChange={(e) => handleFilterChange('category', e.target.value)}
                >
                  <MenuItem value="">All</MenuItem>
                  <MenuItem value="Electronics">Electronics</MenuItem>
                  <MenuItem value="Office Supplies">Office Supplies</MenuItem>
                  <MenuItem value="Construction">Construction</MenuItem>
                  <MenuItem value="IT Services">IT Services</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <FormControl fullWidth size="small">
                <InputLabel>Compliance</InputLabel>
                <Select
                  value={filters.compliance}
                  label="Compliance"
                  onChange={(e) => handleFilterChange('compliance', e.target.value)}
                >
                  <MenuItem value="">All</MenuItem>
                  <MenuItem value="true">Compliant</MenuItem>
                  <MenuItem value="false">Non-Compliant</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6} md={2}>
              <Button
                fullWidth
                variant="contained"
                startIcon={<Search />}
                onClick={handleSearch}
                disabled={loading}
                sx={{
                  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                  boxShadow: '0 4px 12px rgba(102, 126, 234, 0.4)',
                  '&:hover': {
                    boxShadow: '0 6px 16px rgba(102, 126, 234, 0.6)',
                  },
                  textTransform: 'none',
                  fontWeight: 600
                }}
              >
                Search
              </Button>
            </Grid>
          </Grid>
        </Paper>

        <Box sx={{ mb: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h6" sx={{ fontWeight: 600, color: '#2d3748' }}>
            {pagination.totalElements} vendors found
          </Typography>
        </Box>

        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
            <CircularProgress sx={{ color: '#667eea' }} />
          </Box>
        ) : (
          <>
            <Grid container spacing={3}>
              {vendors.map((vendor) => (
                <Grid item xs={12} sm={6} md={4} key={vendor.id}>
                  <Card sx={{ 
                    height: '100%', 
                    borderRadius: 3,
                    boxShadow: '0 4px 12px rgba(0,0,0,0.08)',
                    transition: 'all 0.3s',
                    '&:hover': { 
                      transform: 'translateY(-4px)',
                      boxShadow: '0 8px 24px rgba(102, 126, 234, 0.2)'
                    }
                  }}>
                    <CardContent>
                      <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                        <Box sx={{
                          width: 40,
                          height: 40,
                          borderRadius: 2,
                          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          mr: 1.5
                        }}>
                          <Business sx={{ color: 'white', fontSize: 20 }} />
                        </Box>
                        <Typography variant="h6" sx={{ fontWeight: 700, color: '#2d3748' }}>
                          {vendor.name}
                        </Typography>
                      </Box>
                      
                      <Box sx={{ mb: 2 }}>
                        <Rating value={vendor.rating || 0} readOnly size="small" precision={0.1} />
                        <Typography variant="body2" component="span" sx={{ ml: 1, color: 'text.secondary' }}>
                          ({vendor.rating || 'N/A'})
                        </Typography>
                      </Box>

                      <Divider sx={{ my: 2 }} />

                      <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                        <Email sx={{ fontSize: 16, color: 'text.secondary', mr: 1 }} />
                        <Typography variant="body2" color="text.secondary">
                          {vendor.email}
                        </Typography>
                      </Box>
                      
                      <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                        <Phone sx={{ fontSize: 16, color: 'text.secondary', mr: 1 }} />
                        <Typography variant="body2" color="text.secondary">
                          {vendor.phone}
                        </Typography>
                      </Box>
                      
                      <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                        <LocationOn sx={{ fontSize: 16, color: 'text.secondary', mr: 1 }} />
                        <Typography variant="body2" color="text.secondary">
                          {vendor.location}
                        </Typography>
                      </Box>

                      <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                        <Chip 
                          label={vendor.category} 
                          size="small" 
                          color="primary" 
                          variant="outlined"
                        />
                        <Chip 
                          label={vendor.compliance ? 'Compliant' : 'Non-Compliant'} 
                          size="small" 
                          color={vendor.compliance ? 'success' : 'error'}
                        />
                      </Box>
                    </CardContent>
                  </Card>
                </Grid>
              ))}
            </Grid>

            {pagination.totalPages > 1 && (
              <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
                <Pagination
                  count={pagination.totalPages}
                  page={pagination.page + 1}
                  onChange={handlePageChange}
                  color="primary"
                  size="large"
                />
              </Box>
            )}
          </>
        )}
      </Container>
    </Box>
  );
}

export default VendorSearch;
