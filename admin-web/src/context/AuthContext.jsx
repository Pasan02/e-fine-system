/* eslint-disable react-refresh/only-export-components */
// src/context/AuthContext.jsx
import { createContext, useContext, useState } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { authService } from '../services/apiService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem('efine_admin_user');
    const token = localStorage.getItem('adminToken');
    return savedUser && token ? JSON.parse(savedUser) : null;
  });
  const [loading] = useState(false);

  const login = async (username, password) => {
    try {
      const response = await authService.login(username, password);
      
      const userState = {
        accessToken: response.accessToken,
        role: response.role,
        fullName: 'Admin User',
        username: username
      };

      setUser(userState);
      localStorage.setItem('efine_admin_user', JSON.stringify(userState));
      localStorage.setItem('adminToken', response.accessToken);
      return userState;
    } catch (err) {
      console.error('Login error:', err);
      throw err;
    }
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('efine_admin_user');
    localStorage.removeItem('adminToken');
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

// ProtectedRoute component
export const ProtectedRoute = ({ children }) => {
  const { user, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        backgroundColor: '#0a0b10',
        color: '#f3f4f6',
        fontFamily: 'Outfit, sans-serif'
      }}>
        <div style={{ textAlign: 'center' }}>
          <div style={{
            width: '40px',
            height: '40px',
            border: '3px solid rgba(139, 92, 246, 0.2)',
            borderTop: '3px solid #8b5cf6',
            borderRadius: '50%',
            animation: 'spin 1s linear infinite',
            margin: '0 auto 16px'
          }} />
          <p>Verifying Credentials...</p>
          <style dangerouslySetInnerHTML={{__html: `
            @keyframes spin {
              0% { transform: rotate(0deg); }
              100% { transform: rotate(360deg); }
            }
          `}} />
        </div>
      </div>
    );
  }

  if (!user || user.role !== 'ADMIN') {
    // Redirect to login but save the current location to redirect back
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return children;
};
