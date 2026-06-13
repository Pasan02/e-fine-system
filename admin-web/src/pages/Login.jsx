// src/pages/Login.jsx
import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Shield, Lock, User, AlertCircle } from 'lucide-react';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  // Get redirect path, defaults to dashboard
  const from = location.state?.from?.pathname || '/dashboard';

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    if (!username.trim() || !password.trim()) {
      setError('Please fill in all fields');
      return;
    }

    setSubmitting(true);
    try {
      await login(username.trim(), password);
      navigate(from, { replace: true });
    } catch (err) {
      setError(err.message || 'Incorrect credentials. Access denied.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={styles.container}>
      <div className="glass-card animate-fade-in" style={styles.card}>
        <div style={styles.header}>
          <div style={styles.iconContainer}>
            <Shield size={32} style={styles.icon} />
          </div>
          <h2 style={styles.title}>Sri Lanka Police</h2>
          <p style={styles.subtitle}>Traffic Fine Administrative Portal</p>
        </div>

        {error && (
          <div style={styles.errorBanner}>
            <AlertCircle size={18} style={{ flexShrink: 0 }} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} style={styles.form}>
          <div style={styles.inputGroup}>
            <label htmlFor="username">Username</label>
            <div style={styles.inputWrapper}>
              <User size={18} style={styles.inputIcon} />
              <input
                id="username"
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Enter admin username"
                style={styles.input}
                disabled={submitting}
                autoComplete="username"
              />
            </div>
          </div>

          <div style={styles.inputGroup}>
            <label htmlFor="password">Password</label>
            <div style={styles.inputWrapper}>
              <Lock size={18} style={styles.inputIcon} />
              <input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter password"
                style={styles.input}
                disabled={submitting}
                autoComplete="current-password"
              />
            </div>
          </div>

          <button
            type="submit"
            className="btn-primary"
            style={styles.submitBtn}
            disabled={submitting}
          >
            {submitting ? (
              <>
                <div style={styles.spinner} />
                <span>Authenticating...</span>
              </>
            ) : (
              'Sign In to Dashboard'
            )}
          </button>
        </form>

        <div style={styles.footer}>
          <p>Restricted access. Authorized senior officials only.</p>
          <p style={styles.hint}>Please enter your admin credentials</p>
        </div>
      </div>
    </div>
  );
}

const styles = {
  container: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: '100vh',
    width: '100%',
    padding: '20px',
  },
  card: {
    width: '100%',
    maxWidth: '420px',
    padding: '40px 30px',
  },
  header: {
    textAlign: 'center',
    marginBottom: '30px',
  },
  iconContainer: {
    width: '64px',
    height: '64px',
    borderRadius: '16px',
    background: 'rgba(139, 92, 246, 0.15)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    margin: '0 auto 16px',
    border: '1px solid rgba(139, 92, 246, 0.3)',
    boxShadow: '0 0 20px rgba(139, 92, 246, 0.2)',
  },
  icon: {
    color: '#8b5cf6',
  },
  title: {
    fontSize: '24px',
    fontWeight: '700',
    marginBottom: '4px',
    letterSpacing: '-0.02em',
  },
  subtitle: {
    color: '#9ca3af',
    fontSize: '14px',
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '20px',
  },
  inputGroup: {
    display: 'flex',
    flexDirection: 'column',
  },
  inputWrapper: {
    position: 'relative',
    display: 'flex',
    alignItems: 'center',
  },
  inputIcon: {
    position: 'absolute',
    left: '12px',
    color: '#6b7280',
  },
  input: {
    paddingLeft: '42px',
  },
  submitBtn: {
    width: '100%',
    marginTop: '10px',
  },
  errorBanner: {
    display: 'flex',
    alignItems: 'center',
    gap: '10px',
    padding: '12px 16px',
    borderRadius: '8px',
    backgroundColor: 'rgba(239, 68, 68, 0.15)',
    border: '1px solid rgba(239, 68, 68, 0.3)',
    color: '#ef4444',
    fontSize: '13px',
    marginBottom: '20px',
  },
  footer: {
    textAlign: 'center',
    marginTop: '30px',
    color: '#6b7280',
    fontSize: '12px',
    display: 'flex',
    flexDirection: 'column',
    gap: '6px'
  },
  hint: {
    color: '#9ca3af',
    fontSize: '11px',
  },
  spinner: {
    width: '18px',
    height: '18px',
    border: '2px solid rgba(255, 255, 255, 0.3)',
    borderTop: '2px solid white',
    borderRadius: '50%',
    animation: 'spin 0.8s linear infinite',
  }
};
