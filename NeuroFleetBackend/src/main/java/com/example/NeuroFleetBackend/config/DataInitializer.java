package com.example.NeuroFleetBackend.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.NeuroFleetBackend.model.User;
import com.example.NeuroFleetBackend.model.Vehicle;
import com.example.NeuroFleetBackend.repository.UserRepository;
import com.example.NeuroFleetBackend.repository.VehicleRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize default users if database is empty
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
            
            User fleetManager = new User();
            fleetManager.setUsername("manager");
            fleetManager.setPassword(passwordEncoder.encode("manager123"));
            fleetManager.setRole("FLEET_MANAGER");
            userRepository.save(fleetManager);
            
            User driver = new User();
            driver.setUsername("driver");
            driver.setPassword(passwordEncoder.encode("driver123"));
            driver.setRole("DRIVER");
            userRepository.save(driver);
            
            User customer = new User();
            customer.setUsername("customer");
            customer.setPassword(passwordEncoder.encode("customer123"));
            customer.setRole("CUSTOMER");
            userRepository.save(customer);
            
            System.out.println("Default users created:");
            System.out.println("Admin - username: admin, password: admin123");
            System.out.println("Fleet Manager - username: manager, password: manager123");
            System.out.println("Driver - username: driver, password: driver123");
            System.out.println("Customer - username: customer, password: customer123");
        }
        
        // Initialize sample vehicles if database is empty
        if (vehicleRepository.count() == 0) {
            Vehicle v1 = new Vehicle();
            v1.setVehicleNumber("MH-24-AB-1234");
            v1.setModel("Toyota Innova");
            v1.setType("CAR");
            v1.setStatus("AVAILABLE");
            v1.setLatitude(18.5204);  // Latur, Maharashtra
            v1.setLongitude(76.5644);
            v1.setSpeed(0.0);
            v1.setBatteryLevel(null);
            v1.setFuelLevel(85.0);
            vehicleRepository.save(v1);
            
            Vehicle v2 = new Vehicle();
            v2.setVehicleNumber("MH-24-CD-5678");
            v2.setModel("Honda City");
            v2.setType("CAR");
            v2.setStatus("IN_USE");
            v2.setLatitude(18.5310);  // Near Latur
            v2.setLongitude(76.5750);
            v2.setSpeed(45.0);
            v2.setBatteryLevel(null);
            v2.setFuelLevel(60.0);
            vehicleRepository.save(v2);
            
            Vehicle v3 = new Vehicle();
            v3.setVehicleNumber("MH-24-EF-9012");
            v3.setModel("Tata Ace");
            v3.setType("TRUCK");
            v3.setStatus("MAINTENANCE");
            v3.setLatitude(18.5150);  // Latur district
            v3.setLongitude(76.5600);
            v3.setSpeed(0.0);
            v3.setBatteryLevel(null);
            v3.setFuelLevel(40.0);
            vehicleRepository.save(v3);
            
            Vehicle v4 = new Vehicle();
            v4.setVehicleNumber("MH-24-GH-3456");
            v4.setModel("Ola Electric");
            v4.setType("BIKE");
            v4.setStatus("AVAILABLE");
            v4.setLatitude(18.5280);  // Latur city
            v4.setLongitude(76.5720);
            v4.setSpeed(0.0);
            v4.setBatteryLevel(92.0);
            v4.setFuelLevel(null);
            vehicleRepository.save(v4);
            
            System.out.println("Sample vehicles created in database with Maharashtra (Latur) coordinates");
        }
    }
}