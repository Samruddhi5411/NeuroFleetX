import React, { useState } from 'react';
import axios from 'axios';
import './BookingModal.css';

const BookingModal = ({ vehicle, onClose, onSuccess }) => {
  const [formData, setFormData] = useState({
    startTime: '',
    endTime: '',
    pickupLocation: '',
    dropLocation: '',
    remarks: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [estimatedCost, setEstimatedCost] = useState(null);

  const API_URL = 'http://localhost:8085/api';

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    // Calculate estimated cost when dates change
    if ((name === 'startTime' || name === 'endTime') && formData.startTime && formData.endTime) {
      calculateCost();
    }
  };

  const calculateCost = () => {
    if (formData.startTime && formData.endTime) {
      const start = new Date(formData.startTime);
      const end = new Date(formData.endTime);
      const hours = Math.abs(end - start) / 36e5; // Convert to hours
      const days = Math.max(1, hours / 24);
      const cost = days * 500; // ₹500 per day
      setEstimatedCost(cost.toFixed(2));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Validation
    if (!formData.startTime || !formData.endTime) {
      setError('Please select start and end time');
      return;
    }

    if (new Date(formData.startTime) >= new Date(formData.endTime)) {
      setError('End time must be after start time');
      return;
    }

    if (!formData.pickupLocation || !formData.dropLocation) {
      setError('Please enter pickup and drop locations');
      return;
    }

    try {
      setLoading(true);
      setError(null);

      const token = localStorage.getItem('token');

      const bookingData = {
        vehicleId: vehicle.id,
        startTime: formData.startTime,
        endTime: formData.endTime,
        pickupLocation: formData.pickupLocation,
        dropLocation: formData.dropLocation,
        remarks: formData.remarks
      };

      const response = await axios.post(`${API_URL}/bookings`, bookingData, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      console.log('Booking created:', response.data);
      alert('Booking created successfully! Waiting for admin approval.');

      setLoading(false);
      onSuccess();

    } catch (err) {
      console.error('Booking error:', err);
      setError(err.response?.data || 'Failed to create booking');
      setLoading(false);
    }
  };

  // Get minimum date-time (current time)
  const getMinDateTime = () => {
    const now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
    return now.toISOString().slice(0, 16);
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Book Vehicle</h2>
          <button className="close-button" onClick={onClose}>×</button>
        </div>

        <div className="modal-body">
          {/* Vehicle Info */}
          <div className="booking-vehicle-info">
            <h3>{vehicle.model}</h3>
            <p className="vehicle-number">{vehicle.vehicleNumber}</p>
            <p className="vehicle-type">Type: {vehicle.type}</p>
          </div>

          {/* Booking Form */}
          <form onSubmit={handleSubmit} className="booking-form">
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="startTime">Start Date & Time *</label>
                <input
                  type="datetime-local"
                  id="startTime"
                  name="startTime"
                  value={formData.startTime}
                  onChange={handleChange}
                  min={getMinDateTime()}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="endTime">End Date & Time *</label>
                <input
                  type="datetime-local"
                  id="endTime"
                  name="endTime"
                  value={formData.endTime}
                  onChange={handleChange}
                  min={formData.startTime || getMinDateTime()}
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="pickupLocation">Pickup Location *</label>
              <input
                type="text"
                id="pickupLocation"
                name="pickupLocation"
                value={formData.pickupLocation}
                onChange={handleChange}
                placeholder="Enter pickup location"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="dropLocation">Drop Location *</label>
              <input
                type="text"
                id="dropLocation"
                name="dropLocation"
                value={formData.dropLocation}
                onChange={handleChange}
                placeholder="Enter drop location"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="remarks">Additional Remarks (Optional)</label>
              <textarea
                id="remarks"
                name="remarks"
                value={formData.remarks}
                onChange={handleChange}
                placeholder="Any special requirements or notes"
                rows="3"
              />
            </div>

            {/* Estimated Cost */}
            {estimatedCost && (
              <div className="estimated-cost">
                <span className="cost-label">Estimated Cost:</span>
                <span className="cost-value">₹{estimatedCost}</span>
                <span className="cost-note">(₹500 per day)</span>
              </div>
            )}

            {/* Error Message */}
            {error && (
              <div className="error-message">
                <span className="error-icon">⚠️</span>
                {error}
              </div>
            )}

            {/* Form Actions */}
            <div className="form-actions">
              <button
                type="button"
                className="cancel-button"
                onClick={onClose}
                disabled={loading}
              >
                Cancel
              </button>
              <button
                type="submit"
                className="submit-button"
                disabled={loading}
              >
                {loading ? 'Booking...' : 'Confirm Booking'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default BookingModal;