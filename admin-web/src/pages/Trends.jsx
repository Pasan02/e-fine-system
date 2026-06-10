// src/pages/Trends.jsx
import { useState, useEffect } from 'react';
import { mockAdminService } from '../services/mockApiService';
import { Line } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
} from 'chart.js';
import { 
  TrendingUp, 
  TrendingDown, 
  Activity, 
  Calendar, 
  ArrowUpRight 
} from 'lucide-react';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
);

export default function Trends() {
  const [trends, setTrends] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchData() {
      try {
        const trendsData = await mockAdminService.getCollectionTrends();
        setTrends(trendsData);
      } catch (err) {
        console.error('Error fetching trends data:', err);
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, []);

  if (loading || trends.length === 0) {
    return (
      <div style={styles.loadingContainer}>
        <div style={styles.spinner} />
        <p>Loading Financial Trend Analytics...</p>
      </div>
    );
  }

  // Calculate Key metrics
  const totalRevenue = trends.reduce((sum, t) => sum + t.revenue, 0);
  const averageDailyRevenue = totalRevenue / trends.length;
  const totalFines = trends.reduce((sum, t) => sum + t.finesCount, 0);
  const averageDailyFines = totalFines / trends.length;

  // Percentage growth between first and last recorded days
  const firstDayRev = trends[0].revenue;
  const lastDayRev = trends[trends.length - 1].revenue;
  const growthRate = (((lastDayRev - firstDayRev) / firstDayRev) * 100).toFixed(1);

  // Chart 1 Data: Revenue Line Chart
  const revenueChartData = {
    labels: trends.map(t => new Date(t.date).toLocaleDateString(undefined, {month: 'short', day: 'numeric'})),
    datasets: [
      {
        label: 'Daily Collection (LKR)',
        data: trends.map(t => t.revenue),
        fill: true,
        backgroundColor: 'rgba(139, 92, 246, 0.15)',
        borderColor: '#8b5cf6',
        borderWidth: 2,
        pointBackgroundColor: '#8b5cf6',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: '#8b5cf6',
        pointRadius: 4,
        tension: 0.35,
      }
    ]
  };

  const revenueChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
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

  // Chart 2 Data: Fines Volume Line Chart
  const volumeChartData = {
    labels: trends.map(t => new Date(t.date).toLocaleDateString(undefined, {month: 'short', day: 'numeric'})),
    datasets: [
      {
        label: 'Citations Issued',
        data: trends.map(t => t.finesCount),
        fill: true,
        backgroundColor: 'rgba(59, 130, 246, 0.15)',
        borderColor: '#3b82f6',
        borderWidth: 2,
        pointBackgroundColor: '#3b82f6',
        pointBorderColor: '#fff',
        pointRadius: 4,
        tension: 0.35,
      }
    ]
  };

  const volumeChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false }
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

  return (
    <div>
      {/* Trend Cards Grid */}
      <section className="dashboard-grid" style={{marginBottom: '30px'}}>
        <div className="glass-card metric-card">
          <div className="metric-header">
            <span className="metric-title">Average Daily Collection</span>
            <div className="metric-icon-wrapper" style={{color: '#8b5cf6', background: 'rgba(139, 92, 246, 0.1)'}}>
              <TrendingUp size={20} />
            </div>
          </div>
          <div className="metric-value">Rs. {Math.round(averageDailyRevenue).toLocaleString()}</div>
          <div className="metric-footer">
            <span>Mean collections over last 10 days</span>
          </div>
        </div>

        <div className="glass-card metric-card">
          <div className="metric-header">
            <span className="metric-title">Enforcement Growth</span>
            <div className="metric-icon-wrapper" style={{
              color: growthRate >= 0 ? '#10b981' : '#ef4444', 
              background: growthRate >= 0 ? 'rgba(16, 185, 129, 0.1)' : 'rgba(239, 68, 68, 0.1)'
            }}>
              {growthRate >= 0 ? <TrendingUp size={20} /> : <TrendingDown size={20} />}
            </div>
          </div>
          <div className="metric-value" style={{color: growthRate >= 0 ? '#10b981' : '#ef4444'}}>
            {growthRate >= 0 ? '+' : ''}{growthRate}%
          </div>
          <div className="metric-footer">
            <span>Since baseline day ({new Date(trends[0].date).toLocaleDateString()})</span>
          </div>
        </div>

        <div className="glass-card metric-card">
          <div className="metric-header">
            <span className="metric-title">Average Citations / Day</span>
            <div className="metric-icon-wrapper" style={{color: '#3b82f6', background: 'rgba(59, 130, 246, 0.1)'}}>
              <Activity size={20} />
            </div>
          </div>
          <div className="metric-value">{averageDailyFines.toFixed(1)}</div>
          <div className="metric-footer">
            <span>Mean daily fine sheets issued</span>
          </div>
        </div>
      </section>

      {/* Main Charts Side-by-Side */}
      <section className="charts-grid">
        <div className="glass-card chart-card">
          <h3 className="chart-title">
            <TrendingUp size={18} style={{color: '#8b5cf6'}} />
            <span>Revenue Growth Trend</span>
          </h3>
          <div style={{height: '280px', position: 'relative'}}>
            <Line data={revenueChartData} options={revenueChartOptions} />
          </div>
        </div>

        <div className="glass-card chart-card">
          <h3 className="chart-title">
            <Calendar size={18} style={{color: '#3b82f6'}} />
            <span>Citation Issuance Volume</span>
          </h3>
          <div style={{height: '280px', position: 'relative'}}>
            <Line data={volumeChartData} options={volumeChartOptions} />
          </div>
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
  }
};
