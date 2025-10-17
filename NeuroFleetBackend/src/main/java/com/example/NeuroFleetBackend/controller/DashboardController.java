package com.example.NeuroFleetBackend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.NeuroFleetBackend.repository.UserRepository;
import com.example.NeuroFleetBackend.repository.VehicleRepository;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdminDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        dashboard.put("totalVehicles", vehicleRepository.count());
        dashboard.put("availableVehicles", vehicleRepository.findByStatus("AVAILABLE").size());
        dashboard.put("inUseVehicles", vehicleRepository.findByStatus("IN_USE").size());
        dashboard.put("maintenanceVehicles", vehicleRepository.findByStatus("MAINTENANCE").size());
        dashboard.put("totalUsers", userRepository.count());
        dashboard.put("role", "ADMIN");
        
        System.out.println("Admin dashboard accessed");
        return ResponseEntity.ok(dashboard);
    }
    
    @GetMapping("/fleet-manager")
    @PreAuthorize("hasAuthority('FLEET_MANAGER')")
    public ResponseEntity<Map<String, Object>> getFleetManagerDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        dashboard.put("totalVehicles", vehicleRepository.count());
        dashboard.put("availableVehicles", vehicleRepository.findByStatus("AVAILABLE").size());
        dashboard.put("inUseVehicles", vehicleRepository.findByStatus("IN_USE").size());
        dashboard.put("maintenanceVehicles", vehicleRepository.findByStatus("MAINTENANCE").size());
        dashboard.put("role", "FLEET_MANAGER");
        
        System.out.println("Fleet Manager dashboard accessed");
        return ResponseEntity.ok(dashboard);
    }
    
    @GetMapping("/driver")
    @PreAuthorize("hasAuthority('DRIVER')")
    public ResponseEntity<Map<String, Object>> getDriverDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        dashboard.put("availableVehicles", vehicleRepository.findByStatus("AVAILABLE").size());
        dashboard.put("role", "DRIVER");
        
        System.out.println("Driver dashboard accessed");
        return ResponseEntity.ok(dashboard);
    }
    
    @GetMapping("/customer")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Map<String, Object>> getCustomerDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        dashboard.put("availableVehicles", vehicleRepository.findByStatus("AVAILABLE").size());
        dashboard.put("role", "CUSTOMER");
        
        System.out.println("Customer dashboard accessed");
        return ResponseEntity.ok(dashboard);
    }
}