package com.aufsell.Lab1.repository;

import com.aufsell.Lab1.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleTypeRepository extends JpaRepository<VehicleType, Long> {
    VehicleType findByName(String name);
}
