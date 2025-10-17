
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './VehicleList.css';

const VehicleList = () => {
  const [vehicles, setVehicles] = useState([]);
  const [showAddModal, setShowAddModal] = useState(false);
  const [newVehicle, setNewVehicle] = useState({
    vehicleNumber: '',
    model: '',
    type: 'CAR',
    status: 'AVAILABLE',
    latitude: 18.5204,  // Maharashtra - Latur
    longitude: 76.5644, // Maharashtra - Latur
    speed: 0,
    batteryLevel: null,
    fuelLevel: 100
  });
  const navigate = useNavigate();
  const userRole = localStorage.getItem('role');

  useEffect(() => {
    fetchVehicles();
  }, []);

  const fetchVehicles = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get('http://localhost:8085/api/vehicles', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setVehicles(response.data);
    } catch (err) {
      console.error('Error fetching vehicles:', err);
    }
  };

  const handleAddVehicle = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      await axios.post('http://localhost:8085/api/vehicles', newVehicle, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setShowAddModal(false);
      fetchVehicles();
      setNewVehicle({
        vehicleNumber: '',
        model: '',
        type: 'CAR',
        status: 'AVAILABLE',
        latitude: 18.5204,
        longitude: 76.5644,
        speed: 0,
        batteryLevel: null,
        fuelLevel: 100
      });
    } catch (err) {
      alert('Error adding vehicle: ' + (err.response?.data || err.message));
    }
  };

  const handleDeleteVehicle = async (id) => {
    if (!window.confirm('Are you sure you want to delete this vehicle?')) return;

    try {
      const token = localStorage.getItem('token');
      await axios.delete(`http://localhost:8085/api/vehicles/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchVehicles();
    } catch (err) {
      alert('Error deleting vehicle: ' + (err.response?.data || err.message));
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'AVAILABLE': return '#4caf50';
      case 'IN_USE': return '#2196f3';
      case 'MAINTENANCE': return '#ff9800';
      case 'OUT_OF_SERVICE': return '#f44336';
      default: return '#757575';
    }
  };

  return (
    <div className="vehicle-container">
      <header className="vehicle-header">
        <h1>NeuroFleetX</h1>
        <div className="header-actions">
          {(userRole === 'ADMIN' || userRole === 'FLEET_MANAGER') && (
            <button onClick={() => setShowAddModal(true)} className="add-btn">
              + Add Vehicle
            </button>
          )}
          <button onClick={() => navigate(-1)} className="back-btn">
            Back to Dashboard
          </button>
        </div>
      </header>

      <div className="vehicles-grid">
        {vehicles.map(vehicle => (
          <div key={vehicle.id} className="vehicle-card">
            <div className="vehicle-header-card">
              <h3>{vehicle.vehicleNumber}</h3>
              <span
                className="status-badge"
                style={{ backgroundColor: getStatusColor(vehicle.status) }}
              >
                {vehicle.status}
              </span>
            </div>

            <div className="vehicle-details">
              <p><strong>Model:</strong> {vehicle.model}</p>
              <p><strong>Type:</strong> {vehicle.type}</p>
            </div>

            <div className="telemetry-section">
              <h4>📍 Vehicle Data</h4>
              <div className="telemetry-grid">
                <div className="telemetry-item">
                  <span className="telemetry-icon">📍</span>
                  <div>
                    <p className="telemetry-label">Location</p>
                    <p className="telemetry-value">
                      {vehicle.latitude?.toFixed(4)}, {vehicle.longitude?.toFixed(4)}
                    </p>
                  </div>
                </div>

                <div className="telemetry-item">
                  <span className="telemetry-icon">⚡</span>
                  <div>
                    <p className="telemetry-label">Speed</p>
                    <p className="telemetry-value">{vehicle.speed} km/h</p>
                  </div>
                </div>

                {vehicle.batteryLevel !== null && (
                  <div className="telemetry-item">
                    <span className="telemetry-icon">🔋</span>
                    <div>
                      <p className="telemetry-label">Battery</p>
                      <div className="progress-bar">
                        <div
                          className="progress-fill battery"
                          style={{ width: `${vehicle.batteryLevel}%` }}
                        />
                      </div>
                      <p className="telemetry-value">{vehicle.batteryLevel}%</p>
                    </div>
                  </div>
                )}

                {vehicle.fuelLevel !== null && (
                  <div className="telemetry-item">
                    <span className="telemetry-icon">⛽</span>
                    <div>
                      <p className="telemetry-label">Fuel</p>
                      <div className="progress-bar">
                        <div
                          className="progress-fill fuel"
                          style={{ width: `${vehicle.fuelLevel}%` }}
                        />
                      </div>
                      <p className="telemetry-value">{vehicle.fuelLevel}%</p>
                    </div>
                  </div>
                )}
              </div>
            </div>

            {userRole === 'ADMIN' && (
              <div className="vehicle-actions">
                <button
                  onClick={() => handleDeleteVehicle(vehicle.id)}
                  className="delete-btn"
                >
                  Delete
                </button>
              </div>
            )}
          </div>
        ))}
      </div>

      {showAddModal && (
        <div className="modal-overlay" onClick={() => setShowAddModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h2>Add New Vehicle</h2>
            <form onSubmit={handleAddVehicle}>
              <input
                type="text"
                placeholder="Vehicle Number (e.g., MH-20-AB-1234)"
                value={newVehicle.vehicleNumber}
                onChange={(e) => setNewVehicle({ ...newVehicle, vehicleNumber: e.target.value })}
                required
              />
              <input
                type="text"
                placeholder="Model (e.g., Toyota Innova)"
                value={newVehicle.model}
                onChange={(e) => setNewVehicle({ ...newVehicle, model: e.target.value })}
                required
              />
              <select
                value={newVehicle.type}
                onChange={(e) => setNewVehicle({ ...newVehicle, type: e.target.value })}
              >
                <option value="CAR">Car</option>
                <option value="BIKE">Bike</option>
                <option value="TRUCK">Truck</option>
                <option value="VAN">Van</option>
              </select>
              <select
                value={newVehicle.status}
                onChange={(e) => setNewVehicle({ ...newVehicle, status: e.target.value })}
              >
                <option value="AVAILABLE">Available</option>
                <option value="IN_USE">In Use</option>
                <option value="MAINTENANCE">Maintenance</option>
                <option value="OUT_OF_SERVICE">Out of Service</option>
              </select>
              <div className="form-row">
                <input
                  type="number"
                  step="0.0001"
                  placeholder="Latitude"
                  value={newVehicle.latitude}
                  onChange={(e) => setNewVehicle({ ...newVehicle, latitude: parseFloat(e.target.value) })}
                />
                <input
                  type="number"
                  step="0.0001"
                  placeholder="Longitude"
                  value={newVehicle.longitude}
                  onChange={(e) => setNewVehicle({ ...newVehicle, longitude: parseFloat(e.target.value) })}
                />
              </div>
              <input
                type="number"
                placeholder="Fuel Level (%)"
                value={newVehicle.fuelLevel || ''}
                onChange={(e) => setNewVehicle({ ...newVehicle, fuelLevel: parseFloat(e.target.value) })}
              />
              <div className="modal-actions">
                <button type="submit" className="submit-btn">Add Vehicle</button>
                <button type="button" onClick={() => setShowAddModal(false)} className="cancel-btn">
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default VehicleList;