import React, { useState } from 'react';
import {
  Container,
  Typography,
  Button,
  Grid,
  Card,
  CardContent,
  Box,
  AppBar,
  Toolbar,
  IconButton,
  Alert,
  CircularProgress,
  Paper,
  Divider
} from '@mui/material';
import { ArrowBack, PictureAsPdf, TableChart, Assessment } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { reportAPI } from '../services/api';

function Reports() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState({});
  const [error, setError] = useState('');

  const downloadReport = async (reportType, format) => {
    const loadingKey = `${reportType}_${format}`;
    setLoading(prev => ({ ...prev, [loadingKey]: true }));
    setError('');

    try {
      let response;
      const params = { format };

      console.log('Downloading report:', reportType, format);

      switch (reportType) {
        case 'vendor':
          response = await reportAPI.vendor(params);
          break;
        case 'pr':
          response = await reportAPI.pr(params);
          break;
        case 'po':
          response = await reportAPI.po(params);
          break;
        default:
          throw new Error('Invalid report type');
      }

      console.log('Report response:', response);

      const blob = new Blob([response.data]);
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `${reportType}_report.${format === 'excel' ? 'xlsx' : 'pdf'}`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);

    } catch (error) {
      console.error('Error downloading report:', error);
      console.error('Error response:', error.response);
      setError(error.response?.data?.message || error.message || 'Failed to download report. Please try again.');
    } finally {
      setLoading(prev => ({ ...prev, [loadingKey]: false }));
    }
  };

  const reportCards = [
    {
      title: 'Vendor Report',
      description: 'Comprehensive vendor performance and compliance reports',
      type: 'vendor',
      icon: '🏢',
      color: '#1976d2'
    },
    {
      title: 'Purchase Requisition Report',
      description: 'Track and analyze purchase requisition workflows',
      type: 'pr',
      icon: '📋',
      color: '#388e3c'
    },
    {
      title: 'Purchase Order Report',
      description: 'Monitor purchase orders and delivery status',
      type: 'po',
      icon: '🛒',
      color: '#f57c00'
    }
  ];

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
          <Assessment sx={{ ml: 1, mr: 2, fontSize: 28 }} />
          <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 700 }}>
            Reports & Analytics
          </Typography>
        </Toolbar>
      </AppBar>

      <Container maxWidth="xl" sx={{ mt: 3, mb: 4 }}>
        {error && (
          <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }} onClose={() => setError('')}>
            {error}
          </Alert>
        )}

        <Box sx={{ mb: 3 }}>
          <Typography variant="h4" gutterBottom sx={{ fontWeight: 700, color: '#2d3748' }}>
            Generate Reports
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 500 }}>
            Download comprehensive reports in PDF or Excel format
          </Typography>
        </Box>

        <Grid container spacing={3}>
          {reportCards.map((report) => (
            <Grid item xs={12} key={report.type}>
              <Card sx={{ borderRadius: 3, boxShadow: '0 4px 12px rgba(0,0,0,0.08)', transition: 'all 0.3s', '&:hover': { transform: 'translateY(-4px)', boxShadow: '0 8px 24px rgba(102, 126, 234, 0.2)' } }}>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'flex-start', mb: 3 }}>
                    <Box
                      sx={{
                        width: 60,
                        height: 60,
                        borderRadius: 2,
                        background: `linear-gradient(135deg, ${report.color}20 0%, ${report.color}10 100%)`,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontSize: 32,
                        mr: 2
                      }}
                    >
                      {report.icon}
                    </Box>
                    <Box sx={{ flex: 1 }}>
                      <Typography variant="h5" gutterBottom sx={{ fontWeight: 700, color: '#2d3748' }}>
                        {report.title}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {report.description}
                      </Typography>
                    </Box>
                  </Box>

                  <Divider sx={{ my: 2 }} />

                  <Box sx={{ display: 'flex', gap: 2 }}>
                    <Button
                      variant="contained"
                      startIcon={loading[`${report.type}_pdf`] ? <CircularProgress size={20} /> : <PictureAsPdf />}
                      onClick={() => downloadReport(report.type, 'pdf')}
                      disabled={loading[`${report.type}_pdf`]}
                      color="error"
                      sx={{ minWidth: 160 }}
                    >
                      {loading[`${report.type}_pdf`] ? 'Generating...' : 'Download Report (TXT)'}
                    </Button>
                    <Button
                      variant="contained"
                      startIcon={loading[`${report.type}_excel`] ? <CircularProgress size={20} /> : <TableChart />}
                      onClick={() => downloadReport(report.type, 'excel')}
                      disabled={loading[`${report.type}_excel`]}
                      color="success"
                      sx={{ minWidth: 160 }}
                    >
                      {loading[`${report.type}_excel`] ? 'Generating...' : 'Download Report (CSV)'}
                    </Button>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>

        <Paper sx={{ p: 3, mt: 4, borderRadius: 3, boxShadow: '0 4px 12px rgba(0,0,0,0.08)' }}>
          <Typography variant="h6" gutterBottom sx={{ fontWeight: 700, color: '#2d3748' }}>
            Report Features
          </Typography>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6} md={3}>
              <Box sx={{ textAlign: 'center', p: 2 }}>
                <Typography variant="h4">📊</Typography>
                <Typography variant="body2" color="text.secondary">
                  Comprehensive Analytics
                </Typography>
              </Box>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Box sx={{ textAlign: 'center', p: 2 }}>
                <Typography variant="h4">📈</Typography>
                <Typography variant="body2" color="text.secondary">
                  Performance Metrics
                </Typography>
              </Box>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Box sx={{ textAlign: 'center', p: 2 }}>
                <Typography variant="h4">💾</Typography>
                <Typography variant="body2" color="text.secondary">
                  Multiple Formats
                </Typography>
              </Box>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Box sx={{ textAlign: 'center', p: 2 }}>
                <Typography variant="h4">⚡</Typography>
                <Typography variant="body2" color="text.secondary">
                  Instant Generation
                </Typography>
              </Box>
            </Grid>
          </Grid>
        </Paper>
      </Container>
    </Box>
  );
}

export default Reports;
