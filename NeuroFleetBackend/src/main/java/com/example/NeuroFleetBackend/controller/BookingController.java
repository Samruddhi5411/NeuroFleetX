package com.example.NeuroFleetBackend.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.NeuroFleetBackend.dto.BookingRequest;
import com.example.NeuroFleetBackend.model.Booking;
import com.example.NeuroFleetBackend.model.User;
import com.example.NeuroFleetBackend.model.Vehicle;
import com.example.NeuroFleetBackend.repository.BookingRepository;
import com.example.NeuroFleetBackend.repository.UserRepository;
import com.example.NeuroFleetBackend.repository.VehicleRepository;
import com.example.NeuroFleetBackend.security.JwtUtil;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    // Create a new booking (Customer and Driver can book)
    @PostMapping
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'DRIVER', 'ADMIN')")
    public ResponseEntity<?> createBooking(
            @RequestBody BookingRequest bookingRequest,
            @RequestHeader("Authorization") String authHeader) {
        
        // Extract username from token
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        
        // Find user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Find vehicle
        Vehicle vehicle = vehicleRepository.findById(bookingRequest.getVehicleId())
                .orElse(null);
        
        if (vehicle == null) {
            return ResponseEntity.badRequest().body("Vehicle not found");
        }
        
        // Check if vehicle is available
        if (!"AVAILABLE".equals(vehicle.getStatus())) {
            return ResponseEntity.badRequest()
                    .body("Vehicle is not available for booking");
        }
        
        // Create booking
        Booking booking = new Booking();
        booking.setVehicle(vehicle);
        booking.setUser(user);
        booking.setStartTime(bookingRequest.getStartTime());
        booking.setEndTime(bookingRequest.getEndTime());
        booking.setPickupLocation(bookingRequest.getPickupLocation());
        booking.setDropLocation(bookingRequest.getDropLocation());
        booking.setRemarks(bookingRequest.getRemarks());
        booking.setStatus("PENDING");
        
        // Calculate estimated cost (basic calculation: ₹500 per day)
        if (bookingRequest.getStartTime() != null && bookingRequest.getEndTime() != null) {
            long hours = java.time.Duration.between(
                    bookingRequest.getStartTime(), 
                    bookingRequest.getEndTime()
            ).toHours();
            double days = Math.max(1, hours / 24.0);
            booking.setEstimatedCost(days * 500.0);
        }
        
        // Update vehicle status
        vehicle.setStatus("BOOKED");
        vehicleRepository.save(vehicle);
        
        // Save booking
        Booking savedBooking = bookingRepository.save(booking);
        
        System.out.println("Booking created: " + savedBooking.getId() + 
                         " by user: " + user.getUsername());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
    }
    
    // Get all bookings (Admin and Fleet Manager)
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FLEET_MANAGER')")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingRepository.findAll());
    }
    
    // Get my bookings (Current user)
    @GetMapping("/my-bookings")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'DRIVER', 'ADMIN')")
    public ResponseEntity<?> getMyBookings(
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Booking> bookings = bookingRepository.findByUserId(user.getId());
        return ResponseEntity.ok(bookings);
    }
    
    // Get booking by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FLEET_MANAGER', 'CUSTOMER', 'DRIVER')")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        return bookingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Update booking status
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FLEET_MANAGER')")
    public ResponseEntity<?> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        return bookingRepository.findById(id)
                .map(booking -> {
                    String oldStatus = booking.getStatus();
                    booking.setStatus(status.toUpperCase());
                    
                    // If booking is completed or cancelled, make vehicle available
                    if ("COMPLETED".equals(status.toUpperCase()) || 
                        "CANCELLED".equals(status.toUpperCase())) {
                        Vehicle vehicle = booking.getVehicle();
                        vehicle.setStatus("AVAILABLE");
                        vehicleRepository.save(vehicle);
                    }
                    
                    // If booking is confirmed, mark vehicle as in use
                    if ("CONFIRMED".equals(status.toUpperCase()) || 
                        "IN_PROGRESS".equals(status.toUpperCase())) {
                        Vehicle vehicle = booking.getVehicle();
                        vehicle.setStatus("IN_USE");
                        vehicleRepository.save(vehicle);
                    }
                    
                    Booking updated = bookingRepository.save(booking);
                    System.out.println("Booking " + id + " status changed from " + 
                                     oldStatus + " to " + status);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Cancel booking (Customer can cancel their own booking)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CUSTOMER', 'DRIVER')")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return bookingRepository.findById(id)
                .map(booking -> {
                    // Check if user owns this booking or is admin
                    if (!booking.getUser().getId().equals(user.getId()) && 
                        !"ADMIN".equals(role)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("You can only cancel your own bookings");
                    }
                    
                    // Update booking status
                    booking.setStatus("CANCELLED");
                    bookingRepository.save(booking);
                    
                    // Make vehicle available again
                    Vehicle vehicle = booking.getVehicle();
                    vehicle.setStatus("AVAILABLE");
                    vehicleRepository.save(vehicle);
                    
                    System.out.println("Booking " + id + " cancelled by " + username);
                    return ResponseEntity.ok("Booking cancelled successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Get bookings by status
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'FLEET_MANAGER')")
    public ResponseEntity<List<Booking>> getBookingsByStatus(@PathVariable String status) {
        List<Booking> bookings = bookingRepository.findByStatus(status.toUpperCase());
        return ResponseEntity.ok(bookings);
    }
}