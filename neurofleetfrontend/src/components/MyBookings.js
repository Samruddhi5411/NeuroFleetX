import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './MyBookings.css';

const MyBookings = ({ onRefresh }) => {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const API_URL = 'http://localhost:8085/api';

  useEffect(() => {
    fetchMyBookings();
  }, []);

  const fetchMyBookings = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem('token');

      const response = await axios.get(`${API_URL}/bookings/my-bookings`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      setBookings(response.data);
      setLoading(false);
    } catch (err) {
      console.error('Error fetching bookings:', err);
      setError('Failed to load bookings');
      setLoading(false);
    }
  };

  const handleCancelBooking = async (bookingId) => {
    if (!window.confirm('Are you sure you want to cancel this booking?')) {
      return;
    }

    try {
      const token = localStorage.getItem('token');

      await axios.delete(`${API_URL}/bookings/${bookingId}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      alert('Booking cancelled successfully');
      fetchMyBookings(); // Refresh list
      if (onRefresh) onRefresh(); // Refresh parent dashboard

    } catch (err) {
      console.error('Cancel booking error:', err);
      alert(err.response?.data || 'Failed to cancel booking');
    }
  };

  const getStatusBadge = (status) => {
    const statusClasses = {
      'PENDING': 'status-pending',
      'CONFIRMED': 'status-confirmed',
      'IN_PROGRESS': 'status-inprogress',
      'COMPLETED': 'status-completed',
      'CANCELLED': 'status-cancelled'
    };

    const statusIcons = {
      'PENDING': '⏳',
      'CONFIRMED': '✅',
      'IN_PROGRESS': '🚗',
      'COMPLETED': '✔️',
      'CANCELLED': '❌'
    };

    return (
      <span className={`status-badge ${statusClasses[status]}`}>
        <span className="status-icon">{statusIcons[status]}</span>
        {status}
      </span>
    );
  };

  const formatDateTime = (dateTimeString) => {
    if (!dateTimeString) return 'N/A';
    const date = new Date(dateTimeString);
    return date.toLocaleString('en-IN', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Loading bookings...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-container">
        <p className="error-message">{error}</p>
        <button onClick={fetchMyBookings} className="retry-button">
          Retry
        </button>
      </div>
    );
  }

  if (bookings.length === 0) {
    return (
      <div className="no-bookings">
        <div className="no-bookings-icon">📋</div>
        <h3>No Bookings Yet</h3>
        <p>Book a vehicle to get started</p>
      </div>
    );
  }

  return (
    <div className="my-bookings">
      <div className="bookings-list">
        {bookings.map((booking) => (
          <div key={booking.id} className="booking-card">
            <div className="booking-header">
              <div className="booking-id">Booking #{booking.id}</div>
              {getStatusBadge(booking.status)}
            </div>

            <div className="booking-body">
              {/* Vehicle Info */}
              <div className="booking-section">
                <h4>Vehicle Details</h4>
                <div className="info-grid">
                  <div className="info-item">
                    <span className="label">Model:</span>
                    <span className="value">{booking.vehicle?.model}</span>
                  </div>
                  <div className="info-item">
                    <span className="label">Number:</span>
                    <span className="value">{booking.vehicle?.vehicleNumber}</span>
                  </div>
                  <div className="info-item">
                    <span className="label">Type:</span>
                    <span className="value">{booking.vehicle?.type}</span>
                  </div>
                </div>
              </div>

              {/* Booking Info */}
              <div className="booking-section">
                <h4>Booking Details</h4>
                <div className="info-grid">
                  <div className="info-item">
                    <span className="label">Start Time:</span>
                    <span className="value">{formatDateTime(booking.startTime)}</span>
                  </div>
                  <div className="info-item">
                    <span className="label">End Time:</span>
                    <span className="value">{formatDateTime(booking.endTime)}</span>
                  </div>
                  <div className="info-item">
                    <span className="label">Pickup:</span>
                    <span className="value">{booking.pickupLocation}</span>
                  </div>
                  <div className="info-item">
                    <span className="label">Drop:</span>
                    <span className="value">{booking.dropLocation}</span>
                  </div>
                </div>
              </div>

              {/* Cost Info */}
              {booking.estimatedCost && (
                <div className="booking-section">
                  <h4>Cost Information</h4>
                  <div className="info-grid">
                    <div className="info-item">
                      <span className="label">Estimated Cost:</span>
                      <span className="value cost">₹{booking.estimatedCost}</span>
                    </div>
                    {booking.actualCost && (
                      <div className="info-item">
                        <span className="label">Actual Cost:</span>
                        <span className="value cost">₹{booking.actualCost}</span>
                      </div>
                    )}
                  </div>
                </div>
              )}

              {/* Remarks */}
              {booking.remarks && (
                <div className="booking-section">
                  <h4>Remarks</h4>
                  <p className="remarks">{booking.remarks}</p>
                </div>
              )}
            </div>

            {/* Actions */}
            <div className="booking-footer">
              <div className="booking-time">
                Booked on: {formatDateTime(booking.bookingTime)}
              </div>
              {(booking.status === 'PENDING' || booking.status === 'CONFIRMED') && (
                <button
                  className="cancel-booking-button"
                  onClick={() => handleCancelBooking(booking.id)}
                >
                  Cancel Booking
                </button>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default MyBookings;