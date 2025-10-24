package com.example.NeuroFleetBackend.repository;

import com.example.NeuroFleetBackend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByUserId(Long userId);
    
    List<Booking> findByVehicleId(Long vehicleId);
    
    List<Booking> findByStatus(String status);
    
    List<Booking> findByUserIdAndStatus(Long userId, String status);
}