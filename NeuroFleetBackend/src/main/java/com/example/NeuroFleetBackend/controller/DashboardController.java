package com.example.NeuroFleetBackend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.NeuroFleetBackend.model.User;
import com.example.NeuroFleetBackend.model.Vehicle;
import com.example.NeuroFleetBackend.repository.BookingRepository;
import com.example.NeuroFleetBackend.repository.UserRepository;
import com.example.NeuroFleetBackend.repository.VehicleRepository;
import com.example.NeuroFleetBackend.security.JwtUtil;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdminDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        dashboard.put("totalVehicles", vehicleRepository.count());
        dashboard.put("availableVehicles", vehicleRepository.findByStatus("AVAILABLE").size());
        dashboard.put("inUseVehicles", vehicleRepository.findByStatus("IN_USE").size());
        dashboard.put("maintenanceVehicles", vehicleRepository.findByStatus("MAINTENANCE").size());
        dashboard.put("bookedVehicles", vehicleRepository.findByStatus("BOOKED").size());
        dashboard.put("totalUsers", userRepository.count());
        dashboard.put("totalBookings", bookingRepository.count());
        dashboard.put("pendingBookings", bookingRepository.findByStatus("PENDING").size());
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
        dashboard.put("bookedVehicles", vehicleRepository.findByStatus("BOOKED").size());
        dashboard.put("totalBookings", bookingRepository.count());
        dashboard.put("pendingBookings", bookingRepository.findByStatus("PENDING").size());
        dashboard.put("role", "FLEET_MANAGER");
        
        System.out.println("Fleet Manager dashboard accessed");
        return ResponseEntity.ok(dashboard);
    }
    
    @GetMapping("/driver")
    @PreAuthorize("hasAuthority('DRIVER')")
    public ResponseEntity<Map<String, Object>> getDriverDashboard(
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> dashboard = new HashMap<>();
        
        dashboard.put("availableVehicles", vehicleRepository.findByStatus("AVAILABLE").size());
        dashboard.put("myBookings", bookingRepository.findByUserId(user.getId()).size());
        dashboard.put("myActiveBookings", bookingRepository.findByUserIdAndStatus(user.getId(), "CONFIRMED").size());
        dashboard.put("role", "DRIVER");
        
        System.out.println("Driver dashboard accessed");
        return ResponseEntity.ok(dashboard);
    }
    
    @GetMapping("/customer")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Map<String, Object>> getCustomerDashboard(
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> dashboard = new HashMap<>();
        
        // Get available vehicles
        List<Vehicle> availableVehicles = vehicleRepository.findByStatus("AVAILABLE");
        
        dashboard.put("availableVehicles", availableVehicles.size());
        dashboard.put("vehicles", availableVehicles); // Send full vehicle list
        dashboard.put("myBookings", bookingRepository.findByUserId(user.getId()).size());
        dashboard.put("myActiveBookings", bookingRepository.findByUserIdAndStatus(user.getId(), "CONFIRMED").size());
        dashboard.put("myPendingBookings", bookingRepository.findByUserIdAndStatus(user.getId(), "PENDING").size());
        dashboard.put("role", "CUSTOMER");
        
        System.out.println("Customer dashboard accessed by: " + username);
        return ResponseEntity.ok(dashboard);
    }
    
    // New endpoint: Get available vehicles for customers
    @GetMapping("/customer/available-vehicles")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<List<Vehicle>> getAvailableVehiclesForCustomer() {
        List<Vehicle> availableVehicles = vehicleRepository.findByStatus("AVAILABLE");
        System.out.println("Customer fetching available vehicles: " + availableVehicles.size());
        return ResponseEntity.ok(availableVehicles);
    }
}