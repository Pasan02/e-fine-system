// src/App.jsx
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, ProtectedRoute } from './context/AuthContext';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import DistrictCollections from './pages/DistrictCollections';
import FineCategories from './pages/FineCategories';
import Trends from './pages/Trends';
import SidebarLayout from './components/SidebarLayout';

function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          {/* Public login path */}
          <Route path="/login" element={<Login />} />

          {/* Protected Administrative paths */}
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <SidebarLayout>
                  <Dashboard />
                </SidebarLayout>
              </ProtectedRoute>
            }
          />
          
          <Route
            path="/collections"
            element={
              <ProtectedRoute>
                <SidebarLayout>
                  <DistrictCollections />
                </SidebarLayout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/categories"
            element={
              <ProtectedRoute>
                <SidebarLayout>
                  <FineCategories />
                </SidebarLayout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/trends"
            element={
              <ProtectedRoute>
                <SidebarLayout>
                  <Trends />
                </SidebarLayout>
              </ProtectedRoute>
            }
          />

          {/* Redirect base / wildcard routes to login/dashboard */}
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;
