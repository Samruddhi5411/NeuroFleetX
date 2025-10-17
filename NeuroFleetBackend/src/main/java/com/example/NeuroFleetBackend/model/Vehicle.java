package com.example.NeuroFleetBackend.model;



import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String vehicleNumber;

    @Column(nullable = false)
    private String model;

    private String type; // CAR, BIKE, TRUCK, VAN, etc.

    @Column(nullable = false)
    private String status; // AVAILABLE, IN_USE, MAINTENANCE, OUT_OF_SERVICE

    // Telemetry fields
    private Double latitude;
    private Double longitude;
    private Double speed; // km/h
    private Double batteryLevel; // percentage (0-100) for electric vehicles
    private Double fuelLevel; // percentage (0-100)

    @Column(name = "last_updated")
    private java.time.LocalDateTime lastUpdated;

    // Constructors
    public Vehicle() {}

    public Vehicle(Long id, String vehicleNumber, String model, String type, String status, 
                   Double latitude, Double longitude, Double speed, Double batteryLevel, 
                   Double fuelLevel, java.time.LocalDateTime lastUpdated) {
        this.id = id;
        this.vehicleNumber = vehicleNumber;
        this.model = model;
        this.type = type;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.batteryLevel = batteryLevel;
        this.fuelLevel = fuelLevel;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Double getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(Double fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public java.time.LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(java.time.LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = java.time.LocalDateTime.now();
    }
}