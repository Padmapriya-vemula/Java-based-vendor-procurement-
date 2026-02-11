import React, { useState, useEffect } from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Container,
  Grid,
  Card,
  CardContent,
  Box,
  Avatar,
  Divider,
  CircularProgress
} from '@mui/material';
import {
  Business,
  ShoppingCart,
  Assessment,
  TrendingUp,
  ExitToApp,
  People,
  Receipt
} from '@mui/icons-material';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { statsAPI, dashboardAPI } from '../services/api';
import { formatCurrency } from '../utils/currency';
import BarChartComponent from './charts/BarChartComponent';
import PieChartComponent from './charts/PieChartComponent';
import LineChartComponent from './charts/LineChartComponent';

function Dashboard() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [overview, setOverview] = useState(null);
  const [monthlyTrend, setMonthlyTrend] = useState([]);
  const [prStatus, setPrStatus] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [overviewRes, trendRes, prStatusRes] = await Promise.all([
          dashboardAPI.getOverview(),
          dashboardAPI.getMonthlyTrend(),
          dashboardAPI.getPRStatus()
        ]);
        setOverview(overviewRes.data);
        setMonthlyTrend(trendRes.data);
        setPrStatus(prStatusRes.data.map(item => ({ name: item.status, value: item.count })));
      } catch (error) {
        console.error('Error fetching data:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const menuItems = [
    {
      title: 'Vendor Management',
      description: 'Search, filter, and manage vendor information',
      path: '/vendors',
      color: '#667eea',
      icon: <Business sx={{ fontSize: 48, color: 'white' }} />
    },
    {
      title: 'Purchase Orders',
      description: 'Create and track purchase orders',
      path: '/purchase-orders',
      color: '#f093fb',
      icon: <ShoppingCart sx={{ fontSize: 48, color: 'white' }} />
    },
    {
      title: 'Purchase Requisitions',
      description: 'Approve and manage requisition workflows',
      path: '/workflow',
      color: '#4facfe',
      icon: <Assessment sx={{ fontSize: 48, color: 'white' }} />
    },
    {
      title: 'Reports & Analytics',
      description: 'Generate comprehensive reports',
      path: '/reports',
      color: '#764ba2',
      icon: <TrendingUp sx={{ fontSize: 48, color: 'white' }} />
    }
  ];

  return (
    <Box sx={{ minHeight: '100vh', bgcolor: '#f8f9fa' }}>
      <AppBar 
        position="static" 
        elevation={0}
        sx={{
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          borderBottom: '1px solid rgba(255,255,255,0.1)'
        }}
      >
        <Toolbar sx={{ py: 1 }}>
          <Box
            sx={{
              width: 48,
              height: 48,
              borderRadius: 2,
              bgcolor: 'rgba(255,255,255,0.2)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              mr: 2
            }}
          >
            <Business sx={{ fontSize: 28, color: 'white' }} />
          </Box>
          <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 700, letterSpacing: 0.5 }}>
            Vendor Management System
          </Typography>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <Box
              sx={{
                display: 'flex',
                alignItems: 'center',
                gap: 1,
                bgcolor: 'rgba(255,255,255,0.15)',
                px: 2,
                py: 1,
                borderRadius: 3
              }}
            >
              <Avatar sx={{ width: 32, height: 32, bgcolor: 'rgba(255,255,255,0.3)' }}>
                {user?.email?.charAt(0).toUpperCase()}
              </Avatar>
              <Typography variant="body2" sx={{ fontWeight: 500 }}>
                {user?.email}
              </Typography>
            </Box>
            <Button 
              color="inherit" 
              onClick={handleLogout} 
              startIcon={<ExitToApp />}
              sx={{
                bgcolor: 'rgba(255,255,255,0.15)',
                '&:hover': { bgcolor: 'rgba(255,255,255,0.25)' },
                borderRadius: 2,
                textTransform: 'none',
                fontWeight: 600
              }}
            >
              Logout
            </Button>
          </Box>
        </Toolbar>
      </AppBar>

      <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
            <CircularProgress sx={{ color: '#667eea' }} />
          </Box>
        ) : (
          <>
            {overview && (
              <>
                <Typography variant="h4" gutterBottom sx={{ mb: 4, fontWeight: 700, color: '#2d3748' }}>
                  Analytics Overview
                </Typography>
                <Grid container spacing={3} sx={{ mb: 4 }}>
                  <Grid item xs={12} sm={6} md={3}>
                    <Card sx={{ 
                      borderRadius: 3, 
                      boxShadow: '0 4px 12px rgba(102, 126, 234, 0.15)',
                      transition: 'all 0.3s',
                      '&:hover': { transform: 'translateY(-4px)', boxShadow: '0 8px 24px rgba(102, 126, 234, 0.25)' }
                    }}>
                      <CardContent>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                          <Box sx={{ 
                            width: 56, 
                            height: 56, 
                            borderRadius: 2, 
                            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            boxShadow: '0 4px 12px rgba(102, 126, 234, 0.4)'
                          }}>
                            <People sx={{ fontSize: 32, color: 'white' }} />
                          </Box>
                          <Box>
                            <Typography variant="h4" sx={{ fontWeight: 700, color: '#2d3748' }}>
                              {overview.totalVendors}
                            </Typography>
                            <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 500 }}>
                              Total Vendors
                            </Typography>
                          </Box>
                        </Box>
                      </CardContent>
                    </Card>
                  </Grid>
                  <Grid item xs={12} sm={6} md={3}>
                    <Card sx={{ 
                      borderRadius: 3, 
                      boxShadow: '0 4px 12px rgba(240, 147, 251, 0.15)',
                      transition: 'all 0.3s',
                      '&:hover': { transform: 'translateY(-4px)', boxShadow: '0 8px 24px rgba(240, 147, 251, 0.25)' }
                    }}>
                      <CardContent>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                          <Box sx={{ 
                            width: 56, 
                            height: 56, 
                            borderRadius: 2, 
                            background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            boxShadow: '0 4px 12px rgba(240, 147, 251, 0.4)'
                          }}>
                            <Receipt sx={{ fontSize: 32, color: 'white' }} />
                          </Box>
                          <Box>
                            <Typography variant="h4" sx={{ fontWeight: 700, color: '#2d3748' }}>
                              {overview.totalPRs}
                            </Typography>
                            <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 500 }}>
                              Total PRs
                            </Typography>
                          </Box>
                        </Box>
                      </CardContent>
                    </Card>
                  </Grid>
                  <Grid item xs={12} sm={6} md={3}>
                    <Card sx={{ 
                      borderRadius: 3, 
                      boxShadow: '0 4px 12px rgba(79, 172, 254, 0.15)',
                      transition: 'all 0.3s',
                      '&:hover': { transform: 'translateY(-4px)', boxShadow: '0 8px 24px rgba(79, 172, 254, 0.25)' }
                    }}>
                      <CardContent>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                          <Box sx={{ 
                            width: 56, 
                            height: 56, 
                            borderRadius: 2, 
                            background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            boxShadow: '0 4px 12px rgba(79, 172, 254, 0.4)'
                          }}>
                            <ShoppingCart sx={{ fontSize: 32, color: 'white' }} />
                          </Box>
                          <Box>
                            <Typography variant="h4" sx={{ fontWeight: 700, color: '#2d3748' }}>
                              {overview.totalPOs}
                            </Typography>
                            <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 500 }}>
                              Total POs
                            </Typography>
                          </Box>
                        </Box>
                      </CardContent>
                    </Card>
                  </Grid>
                  <Grid item xs={12} sm={6} md={3}>
                    <Card sx={{ 
                      borderRadius: 3, 
                      boxShadow: '0 4px 12px rgba(168, 237, 234, 0.15)',
                      transition: 'all 0.3s',
                      '&:hover': { transform: 'translateY(-4px)', boxShadow: '0 8px 24px rgba(168, 237, 234, 0.25)' }
                    }}>
                      <CardContent>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                          <Box sx={{ 
                            width: 56, 
                            height: 56, 
                            borderRadius: 2, 
                            background: 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            boxShadow: '0 4px 12px rgba(168, 237, 234, 0.4)'
                          }}>
                            <TrendingUp sx={{ fontSize: 32, color: '#667eea' }} />
                          </Box>
                          <Box>
                            <Typography variant="h5" sx={{ fontWeight: 700, color: '#2d3748' }}>
                              {formatCurrency(overview.totalProcurementAmount)}
                            </Typography>
                            <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 500 }}>
                              Total Amount
                            </Typography>
                          </Box>
                        </Box>
                      </CardContent>
                    </Card>
                  </Grid>
                </Grid>

                <Grid container spacing={3} sx={{ mb: 4 }}>
                  <Grid item xs={12} lg={8}>
                    <BarChartComponent
                      title="PR vs PO Count (Monthly)"
                      data={monthlyTrend}
                      dataKeys={['prCount', 'poCount']}
                      colors={['#f093fb', '#4facfe']}
                    />
                  </Grid>
                  <Grid item xs={12} lg={4}>
                    <PieChartComponent
                      title="PR Status Distribution"
                      data={prStatus}
                      dataKey="value"
                      nameKey="name"
                      colors={['#667eea', '#4facfe', '#f093fb', '#f5576c', '#a8edea']}
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <LineChartComponent
                      title="Monthly Procurement Spending"
                      data={monthlyTrend}
                      dataKey="spending"
                      xAxisKey="month"
                      color="#667eea"
                    />
                  </Grid>
                </Grid>
              </>
            )}

            <Divider sx={{ my: 4, borderColor: 'rgba(0,0,0,0.08)' }} />

            <Typography variant="h4" gutterBottom sx={{ mb: 4, fontWeight: 700, color: '#2d3748' }}>
              Quick Access
            </Typography>

            <Grid container spacing={3}>
              {menuItems.map((item, index) => (
                <Grid item xs={12} sm={6} md={3} key={index}>
                  <Card
                    sx={{
                      cursor: 'pointer',
                      transition: 'all 0.3s ease',
                      background: `linear-gradient(135deg, ${item.color}15 0%, ${item.color}05 100%)`,
                      border: `2px solid ${item.color}30`,
                      height: '100%',
                      borderRadius: 3,
                      '&:hover': {
                        transform: 'translateY(-8px)',
                        boxShadow: `0 12px 24px ${item.color}40`,
                        border: `2px solid ${item.color}`,
                      }
                    }}
                    onClick={() => navigate(item.path)}
                  >
                    <CardContent sx={{ p: 3, textAlign: 'center' }}>
                      <Box
                        sx={{
                          width: 80,
                          height: 80,
                          borderRadius: '50%',
                          bgcolor: item.color,
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          margin: '0 auto 16px',
                          boxShadow: `0 4px 12px ${item.color}60`
                        }}
                      >
                        {item.icon}
                      </Box>
                      <Typography variant="h6" gutterBottom sx={{ fontWeight: 600, color: item.color }}>
                        {item.title}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {item.description}
                      </Typography>
                    </CardContent>
                  </Card>
                </Grid>
              ))}
            </Grid>
          </>
        )}
      </Container>
    </Box>
  );
}

export default Dashboard;
