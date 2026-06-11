// src/pages/DistrictCollections.jsx
import { useState, useEffect } from 'react';
import { mockAdminService } from '../services/mockApiService';
import { Bar } from 'react-chartjs-2';
import {
  Search,
  Download,
  ArrowUpDown,
  Filter,
  MapPin,
  ListFilter
} from 'lucide-react';

export default function DistrictCollections() {
  const [fines, setFines] = useState([]);
  const [districts, setDistricts] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Filtering states
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [districtFilter, setDistrictFilter] = useState('ALL');

  // Sorting state
  const [sortField, setSortField] = useState('issuedAt');
  const [sortDirection, setSortDirection] = useState('desc');

  useEffect(() => {
    async function fetchData() {
      try {
        const finesData = await mockAdminService.getRecentFines();
        const districtsData = await mockAdminService.getDistrictCollections();
        setFines(finesData);
        setDistricts(districtsData);
      } catch (err) {
        console.error('Error fetching district collections:', err);
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, []);

  const handleSort = (field) => {
    if (sortField === field) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
    } else {
      setSortField(field);
      setSortDirection('asc');
    }
  };

  // Filter fines based on criteria
  const filteredFines = fines.filter((fine) => {
    const matchesSearch = 
      fine.referenceNumber.toLowerCase().includes(searchQuery.toLowerCase()) ||
      fine.driverName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      fine.driverLicenseNo.toLowerCase().includes(searchQuery.toLowerCase()) ||
      fine.vehicleNumber.toLowerCase().includes(searchQuery.toLowerCase()) ||
      fine.location.toLowerCase().includes(searchQuery.toLowerCase());

    const matchesStatus = statusFilter === 'ALL' || fine.status === statusFilter;
    const matchesDistrict = districtFilter === 'ALL' || fine.district === districtFilter;

    return matchesSearch && matchesStatus && matchesDistrict;
  });

  // Sort filtered fines
  const sortedFines = [...filteredFines].sort((a, b) => {
    let valueA = a[sortField];
    let valueB = b[sortField];

    if (sortField === 'amount') {
      return sortDirection === 'asc' ? valueA - valueB : valueB - valueA;
    }

    if (typeof valueA === 'string') {
      return sortDirection === 'asc' 
        ? valueA.localeCompare(valueB) 
        : valueB.localeCompare(valueA);
    }

    return 0;
  });

  // Export to CSV Function
  const exportToCSV = () => {
    const headers = ['Reference Number', 'Driver Name', 'License No', 'Vehicle Number', 'District', 'Location', 'Category', 'Amount (LKR)', 'Issued Date', 'Status'];
    const rows = sortedFines.map(fine => [
      fine.referenceNumber,
      `"${fine.driverName.replace(/"/g, '""')}"`,
      fine.driverLicenseNo,
      fine.vehicleNumber,
      fine.district,
      `"${fine.location.replace(/"/g, '""')}"`,
      fine.categoryCode,
      fine.amount,
      fine.issuedAt,
      fine.status
    ]);

    const csvContent = "data:text/csv;charset=utf-8," 
      + [headers.join(','), ...rows.map(e => e.join(','))].join('\n');
    
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement("a");
    link.setAttribute("href", encodedUri);
    link.setAttribute("download", `efine_district_collections_${Date.now()}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  if (loading) {
    return (
      <div style={styles.loadingContainer}>
        <div style={styles.spinner} />
        <p>Loading Regional Analytics...</p>
      </div>
    );
  }

  // Chart Data: Province wise Collections Breakdown
  const provinceChartData = {
    labels: districts.map(d => d.name),
    datasets: [
      {
        label: 'Total Fines Issued',
        data: districts.map(d => d.totalFines),
        backgroundColor: 'rgba(59, 130, 246, 0.5)',
        borderColor: '#3b82f6',
        borderWidth: 1,
        borderRadius: 4,
      },
      {
        label: 'Paid Fines',
        data: districts.map(d => d.paidFines),
        backgroundColor: 'rgba(16, 185, 129, 0.5)',
        borderColor: '#10b981',
        borderWidth: 1,
        borderRadius: 4,
      }
    ]
  };

  const provinceChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        labels: { color: '#f3f4f6', font: { family: 'Plus Jakarta Sans' } }
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

  return (
    <div>
      {/* Province wise Chart Panel */}
      <section className="glass-card" style={{padding: '20px', marginBottom: '30px'}}>
        <h3 className="chart-title" style={{borderBottom: 'none', marginBottom: '10px'}}>
          <MapPin size={18} style={{color: '#8b5cf6'}} />
          <span>Provincial Enforcement Volumes</span>
        </h3>
        <div style={{height: '240px', position: 'relative'}}>
          <Bar data={provinceChartData} options={provinceChartOptions} />
        </div>
      </section>

      {/* Advanced Filter, Search, and Export Bar */}
      <section className="glass-card" style={{padding: '20px', marginBottom: '24px'}}>
        <div style={styles.filterBar}>
          <div style={styles.searchWrapper}>
            <Search size={18} style={styles.searchIcon} />
            <input
              type="text"
              placeholder="Search ref number, name, license, vehicle..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              style={styles.searchInput}
            />
          </div>

          <div style={styles.dropdownsContainer}>
            <div style={styles.selectWrapper}>
              <ListFilter size={16} style={styles.selectIcon} />
              <select 
                value={statusFilter} 
                onChange={(e) => setStatusFilter(e.target.value)}
                style={styles.selectInput}
              >
                <option value="ALL">All Statuses</option>
                <option value="PAID">Paid</option>
                <option value="PENDING">Pending</option>
                <option value="EXPIRED">Expired</option>
              </select>
            </div>

            <div style={styles.selectWrapper}>
              <Filter size={16} style={styles.selectIcon} />
              <select 
                value={districtFilter} 
                onChange={(e) => setDistrictFilter(e.target.value)}
                style={styles.selectInput}
              >
                <option value="ALL">All Provinces</option>
                {districts.map(d => (
                  <option key={d.district} value={d.district}>{d.name}</option>
                ))}
              </select>
            </div>
          </div>

          <button onClick={exportToCSV} className="btn-secondary" style={styles.exportBtn}>
            <Download size={18} />
            <span>Export CSV</span>
          </button>
        </div>
      </section>

      {/* Main Collections Data Grid */}
      <section className="glass-card" style={{padding: '24px'}}>
        <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px'}}>
          <h3 style={{fontSize: '16px'}}>Provincial Violations Registry ({sortedFines.length} records)</h3>
        </div>
        <div className="table-container">
          <table className="custom-table">
            <thead>
              <tr>
                <th onClick={() => handleSort('referenceNumber')} style={styles.sortableHeader}>
                  Ref Number <ArrowUpDown size={14} />
                </th>
                <th>Driver Name</th>
                <th>License</th>
                <th>Vehicle No</th>
                <th onClick={() => handleSort('district')} style={styles.sortableHeader}>
                  Prov <ArrowUpDown size={14} />
                </th>
                <th>Violation Area</th>
                <th onClick={() => handleSort('issuedAt')} style={styles.sortableHeader}>
                  Issued At <ArrowUpDown size={14} />
                </th>
                <th onClick={() => handleSort('amount')} style={styles.sortableHeader}>
                  Fine (LKR) <ArrowUpDown size={14} />
                </th>
                <th onClick={() => handleSort('status')} style={styles.sortableHeader}>
                  Status <ArrowUpDown size={14} />
                </th>
              </tr>
            </thead>
            <tbody>
              {sortedFines.length > 0 ? (
                sortedFines.map((fine) => (
                  <tr key={fine.id}>
                    <td style={{fontFamily: 'monospace', fontWeight: '700'}}>{fine.referenceNumber}</td>
                    <td>{fine.driverName}</td>
                    <td style={{fontFamily: 'monospace'}}>{fine.driverLicenseNo}</td>
                    <td style={{fontFamily: 'monospace'}}>{fine.vehicleNumber}</td>
                    <td style={{fontWeight: '600'}}>{fine.district}</td>
                    <td style={{fontSize: '13px', color: '#9ca3af'}}>{fine.location}</td>
                    <td>{new Date(fine.issuedAt).toLocaleString()}</td>
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
                ))
              ) : (
                <tr>
                  <td colSpan="9" style={{textAlign: 'center', padding: '40px', color: '#6b7280'}}>
                    No violation records match the active search filters.
                  </td>
                </tr>
              )}
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
  filterBar: {
    display: 'flex',
    flexWrap: 'wrap',
    gap: '16px',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  searchWrapper: {
    position: 'relative',
    flexGrow: 1,
    minWidth: '280px',
    display: 'flex',
    alignItems: 'center',
  },
  searchIcon: {
    position: 'absolute',
    left: '12px',
    color: '#6b7280',
  },
  searchInput: {
    paddingLeft: '40px',
  },
  dropdownsContainer: {
    display: 'flex',
    gap: '12px',
  },
  selectWrapper: {
    position: 'relative',
    display: 'flex',
    alignItems: 'center',
    minWidth: '160px',
  },
  selectIcon: {
    position: 'absolute',
    left: '12px',
    color: '#6b7280',
    pointerEvents: 'none',
  },
  selectInput: {
    paddingLeft: '36px',
    cursor: 'pointer',
  },
  exportBtn: {
    height: '44px',
    padding: '0 20px',
  },
  sortableHeader: {
    cursor: 'pointer',
    userSelect: 'none',
    display: 'flex',
    alignItems: 'center',
    gap: '6px',
  }
};
