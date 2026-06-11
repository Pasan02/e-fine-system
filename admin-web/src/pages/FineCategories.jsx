// src/pages/FineCategories.jsx
import { useState, useEffect } from 'react';
import { mockAdminService } from '../services/mockApiService';
import { Doughnut } from 'react-chartjs-2';
import { FileSpreadsheet, Info, ShieldCheck } from 'lucide-react';

export default function FineCategories() {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchData() {
      try {
        const categoriesData = await mockAdminService.getCategoryCollections();
        setCategories(categoriesData);
      } catch (err) {
        console.error('Error fetching categories data:', err);
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, []);

  if (loading) {
    return (
      <div style={styles.loadingContainer}>
        <div style={styles.spinner} />
        <p>Loading Violation Category Diagnostics...</p>
      </div>
    );
  }

  // Chart Data: Top 5 Categories by Revenue
  const topCategories = categories.slice(0, 5);
  const otherCategories = categories.slice(5);
  const otherRevenue = otherCategories.reduce((sum, c) => sum + c.revenue, 0);

  const chartLabels = [...topCategories.map(c => c.code), 'OTHERS'];
  const chartDataValues = [...topCategories.map(c => c.revenue), otherRevenue];

  const categoryChartData = {
    labels: chartLabels,
    datasets: [
      {
        data: chartDataValues,
        backgroundColor: [
          'rgba(139, 92, 246, 0.65)', // purple
          'rgba(59, 130, 246, 0.65)',  // blue
          'rgba(236, 72, 153, 0.65)',  // pink
          'rgba(16, 185, 129, 0.65)',  // emerald
          'rgba(245, 158, 11, 0.65)',  // amber
          'rgba(107, 114, 128, 0.65)'  // gray for others
        ],
        borderColor: [
          '#8b5cf6',
          '#3b82f6',
          '#ec4899',
          '#10b981',
          '#f59e0b',
          '#6b7280'
        ],
        borderWidth: 1,
      }
    ]
  };

  const categoryChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'right',
        labels: {
          color: '#f3f4f6',
          font: { family: 'Plus Jakarta Sans', size: 12 }
        }
      },
      tooltip: {
        callbacks: {
          label: (context) => `Rs. ${context.raw.toLocaleString()}`
        }
      }
    }
  };

  return (
    <div>
      {/* Category Revenue Contribution */}
      <section className="glass-card" style={{padding: '24px', marginBottom: '30px'}}>
        <h3 className="chart-title">
          <FileSpreadsheet size={18} style={{color: '#8b5cf6'}} />
          <span>Category Share - Revenue Contribution</span>
        </h3>
        <div style={{height: '260px', position: 'relative'}}>
          <Doughnut data={categoryChartData} options={categoryChartOptions} />
        </div>
      </section>

      {/* Grid of Fine Categories */}
      <section style={styles.gridHeader}>
        <h3 style={{fontSize: '18px', fontWeight: '700'}}>Statutory Traffic Violations Tariff</h3>
        <span style={{fontSize: '13px', color: '#9ca3af'}}>Configured rates as seeded by national policy</span>
      </section>

      <section style={styles.cardsGrid}>
        {categories.map((category) => (
          <div key={category.code} className="glass-card animate-fade-in" style={styles.categoryCard}>
            <div style={styles.cardHeader}>
              <span style={styles.codeBadge}>{category.code}</span>
              <span style={styles.statusLabel}>
                <ShieldCheck size={14} style={{color: '#10b981'}} />
                <span style={{color: '#10b981', fontSize: '11px', fontWeight: '700'}}>ACTIVE</span>
              </span>
            </div>
            
            <h4 style={styles.cardTitle}>{category.description}</h4>
            
            <div style={styles.metricRow}>
              <div>
                <p style={styles.metricLabel}>Tariff Rate</p>
                <p style={styles.metricValue}>Rs. {category.amount.toLocaleString()}</p>
              </div>
              <div style={{textAlign: 'right'}}>
                <p style={styles.metricLabel}>Citations</p>
                <p style={styles.metricCount}>{category.count}</p>
              </div>
            </div>

            <div style={styles.cardFooter}>
              <Info size={14} style={{color: '#6b7280'}} />
              <span>Total Revenue: Rs. {category.revenue.toLocaleString()}</span>
            </div>
          </div>
        ))}
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
  gridHeader: {
    marginBottom: '20px',
    display: 'flex',
    flexDirection: 'column',
    gap: '4px',
  },
  cardsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
    gap: '20px',
  },
  categoryCard: {
    padding: '20px',
    display: 'flex',
    flexDirection: 'column',
    height: '220px',
    justifyContent: 'space-between',
  },
  cardHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  codeBadge: {
    backgroundColor: 'rgba(139, 92, 246, 0.15)',
    color: '#a78bfa',
    border: '1px solid rgba(139, 92, 246, 0.3)',
    padding: '4px 8px',
    borderRadius: '6px',
    fontFamily: 'monospace',
    fontSize: '12px',
    fontWeight: '700',
  },
  statusLabel: {
    display: 'flex',
    alignItems: 'center',
    gap: '4px',
  },
  cardTitle: {
    fontSize: '14px',
    lineHeight: '1.4',
    margin: '12px 0 16px',
    fontWeight: '600',
    color: '#f3f4f6',
    flexGrow: 1,
  },
  metricRow: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'flex-end',
    borderTop: '1px solid rgba(255, 255, 255, 0.06)',
    paddingTop: '12px',
    marginBottom: '12px',
  },
  metricLabel: {
    fontSize: '10px',
    color: '#6b7280',
    textTransform: 'uppercase',
    fontWeight: '700',
    marginBottom: '2px',
  },
  metricValue: {
    fontSize: '16px',
    fontWeight: '700',
    color: '#f3f4f6',
    fontFamily: 'Outfit, sans-serif',
  },
  metricCount: {
    fontSize: '16px',
    fontWeight: '700',
    color: '#3b82f6',
    fontFamily: 'Outfit, sans-serif',
  },
  cardFooter: {
    display: 'flex',
    alignItems: 'center',
    gap: '6px',
    fontSize: '11px',
    color: '#9ca3af',
  }
};
