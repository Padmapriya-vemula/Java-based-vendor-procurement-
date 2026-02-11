import React, { useState, useEffect } from 'react';
import {
  Container,
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  AppBar,
  Toolbar,
  IconButton,
  CircularProgress,
  Alert
} from '@mui/material';
import { ArrowBack, Dashboard, TrendingUp, People, ShoppingCart, Receipt } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { dashboardAPI } from '../services/dashboardApi';
import BarChartComponent from './charts/BarChartComponent';
import PieChartComponent from './charts/PieChartComponent';
import LineChartComponent from './charts/LineChartComponent';
import DonutChartComponent from './charts/DonutChartComponent';
import { formatCurrency } from '../utils/currency';

function AnalyticsDashboard() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [overview, setOverview] = useState(null);
  const [prStatus, setPrStatus] = useState([]);
  const [poStatus, setPoStatus] = useState([]);
  const [monthlyTrend, setMonthlyTrend] = useState([]);
  const [vendorPOValues, setVendorPOValues] = useState([]);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    setLoading(true);
    setError('');
    try {
      const [overviewRes, prStatusRes, poStatusRes, trendRes, vendorRes] = await Promise.all([
        dashboardAPI.getOverview(),
        dashboardAPI.getPRStatus(),
        dashboardAPI.getPOStatus(),
        dashboardAPI.getMonthlyTrend(),
        dashboardAPI.getVendorPOValues()
      ]);

      setOverview(overviewRes.data);
      setPrStatus(prStatusRes.data.map(item => ({ name: item.status, value: item.count })));
      setPoStatus(poStatusRes.data.map(item => ({ name: item.status, value: item.count })));
      setMonthlyTrend(trendRes.data);
      setVendorPOValues(vendorRes.data.map(item => ({ 
        name: item.vendorName, 
        value: parseFloat(item.totalValue) 
      })));
    } catch (err) {
      setError('Failed to load dashboard data');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const StatCard = ({ title, value, icon, color }) => (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Box>
            <Typography color="text.secondary" variant="body2" gutterBottom>
              {title}
            </Typography>
            <Typography variant="h4" sx={{ fontWeight: 600, color }}>
              {value}
            </Typography>
          </Box>
          <Box sx={{ 
            bgcolor: `${color}15`, 
            borderRadius: 2, 
            p: 1.5,
            display: 'flex',
            alignItems: 'center'
          }}>
            {icon}
          </Box>
        </Box>
      </CardContent>
    </Card>
  );

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
      <AppBar position="static" elevation={0}>
        <Toolbar>
          <IconButton color="inherit" onClick={() => navigate('/')} edge="start">
            <ArrowBack />
          </IconButton>
          <Dashboard sx={{ ml: 1, mr: 2 }} />
          <Typography variant="h6" sx={{ fontWeight: 600 }}>
            Analytics Dashboard
          </Typography>
        </Toolbar>
      </AppBar>

      <Container maxWidth="xl" sx={{ mt: 3, mb: 4 }}>
        {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}

        {overview && (
          <>
            <Grid container spacing={3} sx={{ mb: 3 }}>
              <Grid item xs={12} sm={6} md={3}>
                <StatCard
                  title="Total Vendors"
                  value={overview.totalVendors}
                  icon={<People sx={{ fontSize: 40, color: '#1976d2' }} />}
                  color="#1976d2"
                />
              </Grid>
              <Grid item xs={12} sm={6} md={3}>
                <StatCard
                  title="Active Vendors"
                  value={overview.activeVendors}
                  icon={<People sx={{ fontSize: 40, color: '#2e7d32' }} />}
                  color="#2e7d32"
                />
              </Grid>
              <Grid item xs={12} sm={6} md={3}>
                <StatCard
                  title="Total PRs"
                  value={overview.totalPRs}
                  icon={<Receipt sx={{ fontSize: 40, color: '#ed6c02' }} />}
                  color="#ed6c02"
                />
              </Grid>
              <Grid item xs={12} sm={6} md={3}>
                <StatCard
                  title="Total POs"
                  value={overview.totalPOs}
                  icon={<ShoppingCart sx={{ fontSize: 40, color: '#9c27b0' }} />}
                  color="#9c27b0"
                />
              </Grid>
              <Grid item xs={12}>
                <Card>
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                      <TrendingUp sx={{ fontSize: 40, color: '#2e7d32' }} />
                      <Box>
                        <Typography color="text.secondary" variant="body2">
                          Total Procurement Amount
                        </Typography>
                        <Typography variant="h3" sx={{ fontWeight: 600, color: '#2e7d32' }}>
                          {formatCurrency(overview.totalProcurementAmount)}
                        </Typography>
                      </Box>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            </Grid>

            <Grid container spacing={3}>
              <Grid item xs={12} lg={8}>
                <BarChartComponent
                  title="PR vs PO Count (Monthly)"
                  data={monthlyTrend}
                  dataKeys={['prCount', 'poCount']}
                  colors={['#ed6c02', '#9c27b0']}
                />
              </Grid>
              <Grid item xs={12} lg={4}>
                <PieChartComponent
                  title="PR Status Distribution"
                  data={prStatus}
                  dataKey="value"
                  nameKey="name"
                  colors={['#1976d2', '#2e7d32', '#ed6c02', '#d32f2f', '#757575']}
                />
              </Grid>
              <Grid item xs={12} lg={8}>
                <LineChartComponent
                  title="Monthly Procurement Spending"
                  data={monthlyTrend}
                  dataKey="spending"
                  xAxisKey="month"
                  color="#2e7d32"
                />
              </Grid>
              <Grid item xs={12} lg={4}>
                <DonutChartComponent
                  title="Vendor-wise PO Value"
                  data={vendorPOValues}
                  dataKey="value"
                  nameKey="name"
                  colors={['#1976d2', '#2e7d32', '#ed6c02', '#9c27b0', '#d32f2f', '#0288d1', '#f57c00', '#7b1fa2']}
                />
              </Grid>
              <Grid item xs={12}>
                <BarChartComponent
                  title="PO Status Breakdown"
                  data={poStatus}
                  dataKeys={['value']}
                  colors={['#1976d2']}
                />
              </Grid>
            </Grid>
          </>
        )}
      </Container>
    </Box>
  );
}

export default AnalyticsDashboard;
