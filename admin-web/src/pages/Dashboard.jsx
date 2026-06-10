// src/pages/Dashboard.jsx
import { useState, useEffect } from 'react';
import { mockAdminService } from '../services/mockApiService';
import { Bar, Doughnut } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import { 
  DollarSign, 
  FileCheck, 
  AlertTriangle, 
  Hourglass, 
  Calendar, 
  Activity, 
  ArrowUpRight 
} from 'lucide-react';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  ArcElement,
  Title,
  Tooltip,
  Legend
);

export default function Dashboard() {
  const [stats, setStats] = useState(null);
  const [districts, setDistricts] = useState([]);
  const [recentFines, setRecentFines] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchData() {
      try {
        const statsData = await mockAdminService.getCollectionsSummary();
        const districtsData = await mockAdminService.getDistrictCollections();
        const finesData = await mockAdminService.getRecentFines();
        
        setStats(statsData);
        setDistricts(districtsData);
        setRecentFines(finesData.slice(0, 8)); // Top 8 recent
      } catch (err) {
        console.error('Error fetching dashboard stats:', err);
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, []);

  if (loading || !stats) {
    return (
      <div style={styles.loadingContainer}>
        <div style={styles.spinner} />
        <p>Loading Dashboard Analytics...</p>
      </div>
    );
  }

  // Chart 1 Data: District Collections
  const districtChartData = {
    labels: districts.map(d => d.name.replace(" Province", "")),
    datasets: [
      {
        label: 'Revenue (LKR)',
        data: districts.map(d => d.totalRevenue),
        backgroundColor: 'rgba(139, 92, 246, 0.65)',
        borderColor: '#8b5cf6',
        borderWidth: 1,
        borderRadius: 6,
      }
    ]
  };

  const districtChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false,
      },
      tooltip: {
        callbacks: {
          label: (context) => `Rs. ${context.raw.toLocaleString()}`
        }
      }
    },
    scales: {
      y: {
        grid: { color: 'rgba(255, 255, 255, 0.05)' },
        ticks: { color: '#9ca3af', font: { family: 'Plus Jakarta Sans' } }
      },
      x: {
        grid: { display: false },
        ticks: { color: '#9ca3af', font: { family: 'Plus Jakarta Sans' } }
      }
    }
  };

  // Chart 2 Data: Status Distribution
  const statusChartData = {
    labels: ['Paid', 'Pending', 'Expired'],
    datasets: [
      {
        data: [stats.paidFinesCount, stats.pendingFinesCount, stats.expiredFinesCount],
        backgroundColor: [
          'rgba(16, 185, 129, 0.6)',
          'rgba(245, 158, 11, 0.6)',
          'rgba(239, 68, 68, 0.6)'
        ],
        borderColor: [
          '#10b981',
          '#f59e0b',
          '#ef4444'
        ],
        borderWidth: 1,
      }
    ]
  };

  const statusChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'right',
        labels: {
          color: '#f3f4f6',
          font: { family: 'Plus Jakarta Sans', size: 12 }
        }
      }
    }
  };

  return (
    <div>
      {/* Metric Cards Grid */}
      <section className="dashboard-grid">
        <div className="glass-card metric-card">
          <div className="metric-header">
            <span className="metric-title">Total Revenue</span>
            <div className="metric-icon-wrapper" style={{color: '#10b981', background: 'rgba(16, 185, 129, 0.1)'}}>
              <DollarSign size={20} />
            </div>
          </div>
          <div className="metric-value">Rs. {stats.totalRevenue.toLocaleString()}</div>
          <div className="metric-footer">
            <Activity size={14} style={{color: '#10b981'}} />
            <span>Aggregate collection nationwide</span>
          </div>
        </div>

        <div className="glass-card metric-card">
          <div className="metric-header">
            <span className="metric-title">Fines Issued</span>
            <div className="metric-icon-wrapper" style={{color: '#3b82f6', background: 'rgba(59, 130, 246, 0.1)'}}>
              <FileCheck size={20} />
            </div>
          </div>
          <div className="metric-value">{stats.totalFines.toLocaleString()}</div>
          <div className="metric-footer">
            <Calendar size={14} />
            <span>Cumulative citations</span>
          </div>
        </div>

        <div className="glass-card metric-card paid">
          <div className="metric-header">
            <span className="metric-title">Paid Settlement</span>
            <div className="metric-icon-wrapper" style={{color: '#10b981', background: 'rgba(16, 185, 129, 0.1)'}}>
              <FileCheck size={20} />
            </div>
          </div>
          <div className="metric-value">{stats.paidFinesCount}</div>
          <div className="metric-footer">
            <span>{stats.paidPercentage}% Settlement Rate</span>
          </div>
        </div>

        <div className="glass-card metric-card pending">
          <div className="metric-header">
            <span className="metric-title">Pending Payment</span>
            <div className="metric-icon-wrapper" style={{color: '#f59e0b', background: 'rgba(245, 158, 11, 0.1)'}}>
              <Hourglass size={20} />
            </div>
          </div>
          <div className="metric-value">{stats.pendingFinesCount}</div>
          <div className="metric-footer">
            <span>{stats.pendingPercentage}% Under Notice Period</span>
          </div>
        </div>

        <div className="glass-card metric-card expired">
          <div className="metric-header">
            <span className="metric-title">Expired / Court Ref.</span>
            <div className="metric-icon-wrapper" style={{color: '#ef4444', background: 'rgba(239, 68, 68, 0.1)'}}>
              <AlertTriangle size={20} />
            </div>
          </div>
          <div className="metric-value">{stats.expiredFinesCount}</div>
          <div className="metric-footer">
            <span>{stats.expiredPercentage}% Sent to Magistrate</span>
          </div>
        </div>
      </section>

      {/* Analytics Charts Grid */}
      <section className="charts-grid">
        <div className="glass-card chart-card">
          <h3 className="chart-title">
            <Activity size={18} style={{color: '#8b5cf6'}} />
            <span>Regional Collections (LKR)</span>
          </h3>
          <div style={{height: '280px', position: 'relative'}}>
            <Bar data={districtChartData} options={districtChartOptions} />
          </div>
        </div>

        <div className="glass-card chart-card">
          <h3 className="chart-title">
            <Activity size={18} style={{color: '#3b82f6'}} />
            <span>Citation Settlement Ratio</span>
          </h3>
          <div style={{height: '280px', position: 'relative'}}>
            <Doughnut data={statusChartData} options={statusChartOptions} />
          </div>
        </div>
      </section>

      {/* Recent Violation Citations Table */}
      <section className="glass-card" style={{padding: '24px'}}>
        <div style={styles.tableHeader}>
          <h3 style={{fontSize: '18px', display: 'flex', alignItems: 'center', gap: '8px'}}>
            <span>Latest Violation Citations</span>
          </h3>
          <span style={styles.liveBadge}>
            <span style={styles.liveIndicator}></span>
            <span>Real-time stream</span>
          </span>
        </div>
        <div className="table-container">
          <table className="custom-table">
            <thead>
              <tr>
                <th>Ref Number</th>
                <th>Driver Name</th>
                <th>License</th>
                <th>Vehicle No</th>
                <th>Category</th>
                <th>Issued Date</th>
                <th>Amount</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {recentFines.map((fine) => (
                <tr key={fine.id}>
                  <td style={{fontFamily: 'monospace', fontWeight: '700'}}>{fine.referenceNumber}</td>
                  <td>{fine.driverName}</td>
                  <td style={{fontFamily: 'monospace'}}>{fine.driverLicenseNo}</td>
                  <td style={{fontFamily: 'monospace'}}>{fine.vehicleNumber}</td>
                  <td>{fine.categoryCode}</td>
                  <td>{new Date(fine.issuedAt).toLocaleDateString()}</td>
                  <td style={{fontWeight: '700'}}>Rs. {fine.amount.toLocaleString()}</td>
                  <td>
                    <span className={`badge ${
                      fine.status === 'PAID' ? 'badge-paid' :
                      fine.status === 'PENDING' ? 'badge-pending' : 'badge-expired'
                    }`}>
                      {fine.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}

const styles = {
  loadingContainer: {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    height: '400px',
    gap: '16px',
    color: '#9ca3af'
  },
  spinner: {
    width: '40px',
    height: '40px',
    border: '3px solid rgba(139, 92, 246, 0.2)',
    borderTop: '3px solid #8b5cf6',
    borderRadius: '50%',
    animation: 'spin 1s linear infinite',
  },
  tableHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '20px',
    borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
    paddingBottom: '14px',
  },
  liveBadge: {
    display: 'flex',
    alignItems: 'center',
    gap: '6px',
    fontSize: '11px',
    textTransform: 'uppercase',
    letterSpacing: '0.05em',
    color: '#10b981',
    fontWeight: '700',
    backgroundColor: 'rgba(16, 185, 129, 0.08)',
    padding: '4px 10px',
    borderRadius: '20px',
    border: '1px solid rgba(16, 185, 129, 0.25)',
  },
  liveIndicator: {
    width: '6px',
    height: '6px',
    backgroundColor: '#10b981',
    borderRadius: '50%',
    boxShadow: '0 0 6px #10b981',
    animation: 'pulse 2s infinite',
  }
};
