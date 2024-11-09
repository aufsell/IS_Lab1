package com.aufsell.Lab1.controller;

import com.aufsell.Lab1.dto.CoordinatesDTO;
import com.aufsell.Lab1.dto.VehicleDTO;
import com.aufsell.Lab1.repository.CoordinatesRepository;
import com.aufsell.Lab1.service.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/vehicles/coordinates")
public class CoordinatesController {
    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<List<CoordinatesDTO>> getAllCoordinates() {
        List<CoordinatesDTO> coordinatesDTOS = vehicleService.getAllCoordinates();
        return ResponseEntity.status(HttpStatus.OK).body(coordinatesDTOS);
    }
}
