package com.example.NeuroFleetBackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.NeuroFleetBackend.model.Vehicle;
import com.example.NeuroFleetBackend.repository.VehicleRepository;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    @Autowired
    private VehicleRepository vehicleRepository;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FLEET_MANAGER', 'DRIVER')")
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        System.out.println("Fetching all vehicles");
        return ResponseEntity.ok(vehicleRepository.findAll());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FLEET_MANAGER', 'DRIVER')")
    public ResponseEntity<?> getVehicleById(@PathVariable Long id) {
        return vehicleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FLEET_MANAGER')")
    public ResponseEntity<Vehicle> addVehicle(@RequestBody Vehicle vehicle) {
        // Set default status if not provided
        if (vehicle.getStatus() == null || vehicle.getStatus().isBlank()) {
            vehicle.setStatus("AVAILABLE");
        }
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        System.out.println("Vehicle added: " + savedVehicle.getVehicleNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicle);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FLEET_MANAGER')")
    public ResponseEntity<?> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicleDetails) {
        return vehicleRepository.findById(id)
                .map(vehicle -> {
                    vehicle.setVehicleNumber(vehicleDetails.getVehicleNumber());
                    vehicle.setModel(vehicleDetails.getModel());
                    vehicle.setType(vehicleDetails.getType());
                    vehicle.setStatus(vehicleDetails.getStatus());
                    vehicle.setLatitude(vehicleDetails.getLatitude());
                    vehicle.setLongitude(vehicleDetails.getLongitude());
                    vehicle.setSpeed(vehicleDetails.getSpeed());
                    vehicle.setBatteryLevel(vehicleDetails.getBatteryLevel());
                    vehicle.setFuelLevel(vehicleDetails.getFuelLevel());
                    
                    Vehicle updatedVehicle = vehicleRepository.save(vehicle);
                    System.out.println("Vehicle updated: " + updatedVehicle.getVehicleNumber());
                    return ResponseEntity.ok(updatedVehicle);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
        return vehicleRepository.findById(id)
                .map(vehicle -> {
                    vehicleRepository.delete(vehicle);
                    System.out.println("Vehicle deleted: " + vehicle.getVehicleNumber());
                    return ResponseEntity.ok().body("Vehicle deleted successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FLEET_MANAGER')")
    public ResponseEntity<List<Vehicle>> getVehiclesByStatus(@PathVariable String status) {
        List<Vehicle> vehicles = vehicleRepository.findByStatus(status.toUpperCase());
        return ResponseEntity.ok(vehicles);
    }
    
    @PatchMapping("/{id}/telemetry")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FLEET_MANAGER', 'DRIVER')")
    public ResponseEntity<?> updateTelemetry(
            @PathVariable Long id,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double speed,
            @RequestParam(required = false) Double batteryLevel,
            @RequestParam(required = false) Double fuelLevel) {
        
        return vehicleRepository.findById(id)
                .map(vehicle -> {
                    if (latitude != null) vehicle.setLatitude(latitude);
                    if (longitude != null) vehicle.setLongitude(longitude);
                    if (speed != null) vehicle.setSpeed(speed);
                    if (batteryLevel != null) vehicle.setBatteryLevel(batteryLevel);
                    if (fuelLevel != null) vehicle.setFuelLevel(fuelLevel);
                    
                    Vehicle updated = vehicleRepository.save(vehicle);
                    System.out.println("Telemetry updated for vehicle: " + updated.getVehicleNumber());
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}