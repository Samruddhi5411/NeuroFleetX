import React, { useState } from 'react';
import BookingModal from './BookingModal';
import './AvailableVehicles.css';

const AvailableVehicles = ({ vehicles, onBookingSuccess }) => {
  const [selectedVehicle, setSelectedVehicle] = useState(null);
  const [showModal, setShowModal] = useState(false);

  const handleBookNow = (vehicle) => {
    setSelectedVehicle(vehicle);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedVehicle(null);
  };

  const handleBookingComplete = () => {
    setShowModal(false);
    setSelectedVehicle(null);
    if (onBookingSuccess) {
      onBookingSuccess();
    }
  };

  const getVehicleIcon = (type) => {
    switch (type) {
      case 'CAR': return '🚗';
      case 'BIKE': return '🏍️';
      case 'TRUCK': return '🚚';
      case 'VAN': return '🚐';
      default: return '🚗';
    }
  };

  const getFuelBatteryDisplay = (vehicle) => {
    if (vehicle.batteryLevel !== null && vehicle.batteryLevel !== undefined) {
      return (
        <div className="fuel-info battery">
          <span className="icon">🔋</span>
          <span className="label">Battery:</span>
          <span className="value">{vehicle.batteryLevel}%</span>
          <div className="fuel-bar">
            <div
              className="fuel-fill battery-fill"
              style={{ width: `${vehicle.batteryLevel}%` }}
            ></div>
          </div>
        </div>
      );
    } else if (vehicle.fuelLevel !== null && vehicle.fuelLevel !== undefined) {
      return (
        <div className="fuel-info fuel">
          <span className="icon">⛽</span>
          <span className="label">Fuel:</span>
          <span className="value">{vehicle.fuelLevel}%</span>
          <div className="fuel-bar">
            <div
              className="fuel-fill"
              style={{ width: `${vehicle.fuelLevel}%` }}
            ></div>
          </div>
        </div>
      );
    }
    return null;
  };

  if (!vehicles || vehicles.length === 0) {
    return (
      <div className="no-vehicles">
        <div className="no-vehicles-icon">🚗</div>
        <h3>No Vehicles Available</h3>
        <p>Please check back later for available vehicles</p>
      </div>
    );
  }

  return (
    <div className="available-vehicles">
      <div className="vehicles-grid">
        {vehicles.map((vehicle) => (
          <div key={vehicle.id} className="vehicle-card">
            <div className="vehicle-header">
              <div className="vehicle-icon">{getVehicleIcon(vehicle.type)}</div>
              <span className="vehicle-status available">Available</span>
            </div>

            <div className="vehicle-body">
              <h3 className="vehicle-model">{vehicle.model}</h3>
              <p className="vehicle-number">{vehicle.vehicleNumber}</p>

              <div className="vehicle-details">
                <div className="detail-item">
                  <span className="label">Type:</span>
                  <span className="value">{vehicle.type}</span>
                </div>

                {vehicle.speed !== null && vehicle.speed !== undefined && (
                  <div className="detail-item">
                    <span className="label">Speed:</span>
                    <span className="value">{vehicle.speed} km/h</span>
                  </div>
                )}

                {getFuelBatteryDisplay(vehicle)}
              </div>

              {vehicle.latitude && vehicle.longitude && (
                <div className="location-info">
                  <span className="icon">📍</span>
                  <span className="location-text">
                    {vehicle.latitude.toFixed(4)}, {vehicle.longitude.toFixed(4)}
                  </span>
                </div>
              )}
            </div>

            <div className="vehicle-footer">
              <button
                className="book-button"
                onClick={() => handleBookNow(vehicle)}
              >
                Book Now
              </button>
            </div>
          </div>
        ))}
      </div>

      {showModal && selectedVehicle && (
        <BookingModal
          vehicle={selectedVehicle}
          onClose={handleCloseModal}
          onSuccess={handleBookingComplete}
        />
      )}
    </div>
  );
};

export default AvailableVehicles;