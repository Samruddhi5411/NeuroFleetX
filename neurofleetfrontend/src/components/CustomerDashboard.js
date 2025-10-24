// import React, { useState, useEffect } from 'react';
// import axios from 'axios';
// import AvailableVehicles from './AvailableVehicles';
// import MyBookings from './MyBookings';
// import './Dashboard.css';

// const CustomerDashboard = () => {
//   const [dashboardData, setDashboardData] = useState(null);
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);
//   const [activeTab, setActiveTab] = useState('vehicles'); // 'vehicles' or 'bookings'

//   const API_URL = 'http://localhost:8085/api';

//   useEffect(() => {
//     fetchDashboard();
//   }, []);

//   const fetchDashboard = async () => {
//     try {
//       setLoading(true);
//       const token = localStorage.getItem('token');

//       const response = await axios.get(`${API_URL}/dashboard/customer`, {
//         headers: {
//           'Authorization': `Bearer ${token}`
//         }
//       });

//       setDashboardData(response.data);
//       setLoading(false);
//     } catch (err) {
//       console.error('Error fetching dashboard:', err);
//       setError('Failed to load dashboard data');
//       setLoading(false);
//     }
//   };

//   const handleBookingSuccess = () => {
//     // Refresh dashboard after successful booking
//     fetchDashboard();
//     setActiveTab('bookings'); // Switch to bookings tab
//   };

//   if (loading) {
//     return (
//       <div className="loading-container">
//         <div className="spinner"></div>
//         <p>Loading dashboard...</p>
//       </div>
//     );
//   }

//   if (error) {
//     return (
//       <div className="error-container">
//         <p className="error-message">{error}</p>
//         <button onClick={fetchDashboard} className="retry-button">
//           Retry
//         </button>
//       </div>
//     );
//   }

//   return (
//     <div className="customer-dashboard">
//       <div className="dashboard-header">
//         <h1>Customer Dashboard</h1>
//         <div className="user-info">
//           <span>Welcome, {localStorage.getItem('username') || 'Customer'}</span>
//         </div>
//       </div>

//       {/* Statistics Cards */}
//       <div className="stats-container">
//         <div className="stat-card">
//           <div className="stat-icon">🚗</div>
//           <div className="stat-content">
//             <h3>{dashboardData?.availableVehicles || 0}</h3>
//             <p>Available Vehicles</p>
//           </div>
//         </div>

//         <div className="stat-card">
//           <div className="stat-icon">📋</div>
//           <div className="stat-content">
//             <h3>{dashboardData?.myBookings || 0}</h3>
//             <p>Total Bookings</p>
//           </div>
//         </div>

//         <div className="stat-card">
//           <div className="stat-icon">✅</div>
//           <div className="stat-content">
//             <h3>{dashboardData?.myActiveBookings || 0}</h3>
//             <p>Active Bookings</p>
//           </div>
//         </div>

//         <div className="stat-card">
//           <div className="stat-icon">⏳</div>
//           <div className="stat-content">
//             <h3>{dashboardData?.myPendingBookings || 0}</h3>
//             <p>Pending Bookings</p>
//           </div>
//         </div>
//       </div>

//       {/* Tab Navigation */}
//       <div className="tabs-container">
//         <button
//           className={`tab-button ${activeTab === 'vehicles' ? 'active' : ''}`}
//           onClick={() => setActiveTab('vehicles')}
//         >
//           Available Vehicles
//         </button>
//         <button
//           className={`tab-button ${activeTab === 'bookings' ? 'active' : ''}`}
//           onClick={() => setActiveTab('bookings')}
//         >
//           My Bookings
//         </button>
//       </div>

//       {/* Tab Content */}
//       <div className="tab-content">
//         {activeTab === 'vehicles' ? (
//           <AvailableVehicles
//             vehicles={dashboardData?.vehicles || []}
//             onBookingSuccess={handleBookingSuccess}
//           />
//         ) : (
//           <MyBookings onRefresh={fetchDashboard} />
//         )}
//       </div>
//     </div>
//   );
// };

// export default CustomerDashboard;

import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import AvailableVehicles from './AvailableVehicles';
import MyBookings from './MyBookings';
import './Dashboard.css';

const CustomerDashboard = () => {
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState('vehicles');
  const navigate = useNavigate();

  const API_URL = 'http://localhost:8085/api';

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem('token');

      const response = await axios.get(`${API_URL}/dashboard/customer`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      setDashboardData(response.data);
      setLoading(false);
    } catch (err) {
      console.error('Error fetching dashboard:', err);
      setError('Failed to load dashboard data');
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('username');
    navigate('/login');
  };

  const handleBookingSuccess = () => {
    fetchDashboard();
    setActiveTab('bookings');
  };

  if (loading) {
    return (
      <div className="dashboard">
        <div className="dashboard-content">
          <div className="loading-state">
            <div className="spinner"></div>
            <p>Loading dashboard...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="dashboard">
        <div className="dashboard-content">
          <div className="error-state">
            <p className="error-text">{error}</p>
            <button onClick={fetchDashboard} className="primary-btn">
              Retry
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard">
      {/* Header */}
      <div className="dashboard-header">
        <h1>Customer Dashboard</h1>
        <div className="user-info">
          <span>Welcome, {localStorage.getItem('username') || 'Customer'}</span>
          <button onClick={handleLogout} className="logout-btn">
            Logout
          </button>
        </div>
      </div>

      {/* Main Content */}
      <div className="dashboard-content">
        {/* Statistics Cards */}
        <div className="stats-grid">
          <div className="stat-card available">
            <h3>Available Vehicles</h3>
            <div className="stat-number">{dashboardData?.availableVehicles || 0}</div>
          </div>

          <div className="stat-card">
            <h3>Total Bookings</h3>
            <div className="stat-number">{dashboardData?.myBookings || 0}</div>
          </div>

          <div className="stat-card in-use">
            <h3>Active Bookings</h3>
            <div className="stat-number">{dashboardData?.myActiveBookings || 0}</div>
          </div>

          <div className="stat-card maintenance">
            <h3>Pending Bookings</h3>
            <div className="stat-number">{dashboardData?.myPendingBookings || 0}</div>
          </div>
        </div>

        {/* Tab Navigation */}
        <div className="tabs-section">
          <div className="tabs-header">
            <button
              className={`tab-btn ${activeTab === 'vehicles' ? 'active' : ''}`}
              onClick={() => setActiveTab('vehicles')}
            >
              🚗 Available Vehicles
            </button>
            <button
              className={`tab-btn ${activeTab === 'bookings' ? 'active' : ''}`}
              onClick={() => setActiveTab('bookings')}
            >
              📋 My Bookings
            </button>
          </div>

          {/* Tab Content */}
          <div className="tabs-content">
            {activeTab === 'vehicles' ? (
              <AvailableVehicles
                vehicles={dashboardData?.vehicles || []}
                onBookingSuccess={handleBookingSuccess}
              />
            ) : (
              <MyBookings onRefresh={fetchDashboard} />
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default CustomerDashboard;