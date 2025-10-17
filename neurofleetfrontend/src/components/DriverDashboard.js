import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './Dashboard.css';

const DriverDashboard = () => {
  const [dashboardData, setDashboardData] = useState(null);
  const navigate = useNavigate();
  const username = localStorage.getItem('username');

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get('http://localhost:8085/api/dashboard/driver', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setDashboardData(response.data);
    } catch (err) {
      console.error('Error fetching dashboard:', err);
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <h1>Driver Dashboard</h1>
        <div className="user-info">
          <span>Welcome, {username}</span>
          <button onClick={handleLogout} className="logout-btn">Logout</button>
        </div>
      </header>

      <div className="dashboard-content">
        {dashboardData && (
          <div className="stats-grid">
            <div className="stat-card available">
              <h3>Available Vehicles</h3>
              <p className="stat-number">{dashboardData.availableVehicles}</p>
            </div>
          </div>
        )}

        <div className="action-buttons">
          <button onClick={() => navigate('/vehicles')} className="primary-btn">
            View Vehicles
          </button>
        </div>
      </div>
    </div>
  );
};

export default DriverDashboard;