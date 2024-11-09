package com.aufsell.Lab1.repository;

import com.aufsell.Lab1.model.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuelTypeRepository extends JpaRepository<FuelType, Long> {
    FuelType findByName(String name);
}

