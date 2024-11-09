package com.aufsell.Lab1.repository;

import com.aufsell.Lab1.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Vehicle findByName(String vehicleNo);
    Long countByFuelConsumptionLessThan(double fuelConsumption);
    List<Vehicle> findByNameContainingIgnoreCase(String substring);
    List<Vehicle> findByVehicleTypeId(Long vehicleTypeId);
}
