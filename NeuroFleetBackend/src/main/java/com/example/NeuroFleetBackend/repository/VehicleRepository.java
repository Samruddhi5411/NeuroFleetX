package com.example.NeuroFleetBackend.repository;



import com.example.NeuroFleetBackend.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    List<Vehicle> findByStatus(String status);
    
    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);
    
    List<Vehicle> findByType(String type);
}