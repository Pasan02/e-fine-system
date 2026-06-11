// src/components/SidebarLayout.jsx
import { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard,
  Map,
  FileText,
  TrendingUp,
  LogOut,
  ShieldAlert,
  Menu,
  X,
  UserCheck
} from 'lucide-react';

export default function SidebarLayout({ children }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const navItems = [
    { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { name: 'District Collections', path: '/collections', icon: Map },
    { name: 'Fine Categories', path: '/categories', icon: FileText },
    { name: 'Time Trends', path: '/trends', icon: TrendingUp },
  ];

  return (
    <div style={styles.container}>
      {/* Mobile Menu Toggler */}
      <button 
        onClick={() => setMobileMenuOpen(!mobileMenuOpen)} 
        style={styles.mobileToggle}
        aria-label="Toggle Navigation Menu"
      >
        {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
      </button>

      {/* Sidebar Panel */}
      <aside 
        style={{
          ...styles.sidebar,
          transform: mobileMenuOpen ? 'translateX(0)' : 'translateX(-100%)',
        }}
        className="glass-card"
      >
        <div style={styles.logoContainer}>
          <ShieldAlert size={28} style={styles.logoIcon} />
          <div>
            <h1 style={styles.logoText}>E-Fine Admin</h1>
            <p style={styles.logoSubtext}>National System</p>
          </div>
        </div>

        <nav style={styles.nav}>
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.path}
                to={item.path}
                onClick={() => setMobileMenuOpen(false)}
                style={({ isActive }) => ({
                  ...styles.navLink,
                  ...(isActive ? styles.navLinkActive : {}),
                })}
              >
                {({ isActive }) => (
                  <>
                    <Icon 
                      size={20} 
                      style={{
                        ...styles.navIcon,
                        color: isActive ? '#8b5cf6' : '#9ca3af'
                      }} 
                    />
                    <span>{item.name}</span>
                  </>
                )}
              </NavLink>
            );
          })}
        </nav>

        {/* User profile footer info */}
        <div style={styles.sidebarFooter}>
          <div style={styles.profileBox}>
            <div style={styles.avatar}>
              <UserCheck size={20} style={{ color: '#3b82f6' }} />
            </div>
            <div style={styles.profileDetails}>
              <h4 style={styles.profileName}>{user?.fullName || 'Senior Official'}</h4>
              <p style={styles.profileRole}>{user?.role || 'ADMIN'}</p>
            </div>
          </div>
          <button onClick={handleLogout} style={styles.logoutBtn}>
            <LogOut size={18} />
            <span>Sign Out</span>
          </button>
        </div>
      </aside>

      {/* Backdrop for mobile navigation menu */}
      {mobileMenuOpen && (
        <div 
          onClick={() => setMobileMenuOpen(false)} 
          style={styles.backdrop} 
        />
      )}

      {/* Content Viewport */}
      <main style={styles.mainContent}>
        <header style={styles.topHeader}>
          <div>
            <h2 style={styles.pageTitle}>Administrative Oversight</h2>
            <p style={styles.breadcrumb}>Traffic Fine Payment & Collections Portal</p>
          </div>
          <div style={styles.topHeaderDate}>
            <span style={styles.liveIndicator}></span>
            <span>Live Statistics • Sri Lanka</span>
          </div>
        </header>
        <div style={styles.pageBody} className="animate-fade-in">
          {children}
        </div>
      </main>
      
      {/* Mobile and Desktop responsive style overrides */}
      <style dangerouslySetInnerHTML={{__html: `
        @media (min-width: 1025px) {
          aside {
            transform: translateX(0) !important;
          }
          button[aria-label="Toggle Navigation Menu"] {
            display: none !important;
          }
        }
        @media (max-width: 1024px) {
          main {
            padding-left: 20px !important;
            padding-top: 80px !important;
          }
        }
      `}} />
    </div>
  );
}

const styles = {
  container: {
    display: 'flex',
    minHeight: '100vh',
    width: '100%',
    backgroundColor: '#0a0b10',
    position: 'relative',
  },
  sidebar: {
    width: '280px',
    height: '100vh',
    position: 'fixed',
    top: 0,
    left: 0,
    zIndex: 100,
    display: 'flex',
    flexDirection: 'column',
    padding: '24px 16px',
    borderRadius: '0 16px 16px 0',
    backgroundColor: 'rgba(13, 16, 30, 0.95)',
    transition: 'transform 0.3s ease-in-out',
  },
  logoContainer: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
    padding: '8px 12px 24px',
    borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
    marginBottom: '24px',
  },
  logoIcon: {
    color: '#8b5cf6',
  },
  logoText: {
    fontSize: '18px',
    fontWeight: '800',
    letterSpacing: '-0.02em',
  },
  logoSubtext: {
    color: '#9ca3af',
    fontSize: '11px',
    textTransform: 'uppercase',
    letterSpacing: '0.05em',
  },
  nav: {
    display: 'flex',
    flexDirection: 'column',
    gap: '8px',
    flexGrow: 1,
  },
  navLink: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
    padding: '14px 16px',
    borderRadius: '8px',
    color: '#9ca3af',
    fontSize: '14px',
    fontWeight: '600',
    transition: 'all 0.2s ease',
  },
  navLinkActive: {
    backgroundColor: 'rgba(139, 92, 246, 0.12)',
    color: '#f3f4f6',
    border: '1px solid rgba(139, 92, 246, 0.2)',
  },
  navIcon: {
    transition: 'color 0.2s ease',
  },
  sidebarFooter: {
    marginTop: 'auto',
    borderTop: '1px solid rgba(255, 255, 255, 0.08)',
    paddingTop: '20px',
    display: 'flex',
    flexDirection: 'column',
    gap: '16px',
  },
  profileBox: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
    padding: '8px 12px',
  },
  avatar: {
    width: '40px',
    height: '40px',
    borderRadius: '10px',
    backgroundColor: 'rgba(59, 130, 246, 0.12)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    border: '1px solid rgba(59, 130, 246, 0.2)',
  },
  profileDetails: {
    overflow: 'hidden',
  },
  profileName: {
    fontSize: '14px',
    fontWeight: '600',
    whiteSpace: 'nowrap',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
  },
  profileRole: {
    fontSize: '11px',
    color: '#6b7280',
    textTransform: 'uppercase',
    letterSpacing: '0.05em',
  },
  logoutBtn: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '8px',
    width: '100%',
    padding: '12px',
    borderRadius: '8px',
    border: '1px solid rgba(239, 68, 68, 0.25)',
    backgroundColor: 'rgba(239, 68, 68, 0.08)',
    color: '#ef4444',
    fontFamily: 'Outfit, sans-serif',
    fontWeight: '600',
    cursor: 'pointer',
    transition: 'all 0.2s ease',
  },
  mobileToggle: {
    position: 'fixed',
    top: '20px',
    left: '20px',
    zIndex: 110,
    width: '44px',
    height: '44px',
    borderRadius: '10px',
    backgroundColor: '#121420',
    border: '1px solid rgba(255, 255, 255, 0.1)',
    color: '#f3f4f6',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    cursor: 'pointer',
  },
  backdrop: {
    position: 'fixed',
    top: 0,
    left: 0,
    width: '100vw',
    height: '100vh',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    backdropFilter: 'blur(4px)',
    zIndex: 90,
  },
  mainContent: {
    flexGrow: 1,
    paddingLeft: '300px', // width + spacing of sidebar
    paddingRight: '20px',
    paddingTop: '30px',
    paddingBottom: '40px',
    minHeight: '100vh',
    display: 'flex',
    flexDirection: 'column',
    overflowY: 'auto',
  },
  topHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
    paddingBottom: '20px',
    marginBottom: '30px',
  },
  pageTitle: {
    fontSize: '24px',
    fontWeight: '700',
    letterSpacing: '-0.02em',
  },
  breadcrumb: {
    fontSize: '13px',
    color: '#9ca3af',
  },
  topHeaderDate: {
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
    fontSize: '13px',
    fontWeight: '600',
    color: '#9ca3af',
    backgroundColor: 'rgba(255, 255, 255, 0.04)',
    padding: '8px 16px',
    borderRadius: '20px',
    border: '1px solid rgba(255, 255, 255, 0.06)',
  },
  liveIndicator: {
    width: '8px',
    height: '8px',
    backgroundColor: '#10b981',
    borderRadius: '50%',
    display: 'inline-block',
    boxShadow: '0 0 10px #10b981',
    animation: 'pulse 2s infinite',
  },
  pageBody: {
    flexGrow: 1,
  }
};
