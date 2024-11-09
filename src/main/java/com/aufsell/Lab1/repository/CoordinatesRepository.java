package com.aufsell.Lab1.repository;

import com.aufsell.Lab1.model.Coordinates;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoordinatesRepository extends JpaRepository<Coordinates, Long> {
    Coordinates findById(long id);
}
