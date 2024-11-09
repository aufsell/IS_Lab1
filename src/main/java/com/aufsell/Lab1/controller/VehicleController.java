package com.aufsell.Lab1.controller;

import com.aufsell.Lab1.dto.FuelTypeDTO;
import com.aufsell.Lab1.dto.VehicleDTO;
import com.aufsell.Lab1.dto.VehicleDeleteRequest;
import com.aufsell.Lab1.dto.VehicleTypeDTO;
import com.aufsell.Lab1.exception.ResourceNotFoundException;
import com.aufsell.Lab1.model.FuelType;
import com.aufsell.Lab1.model.VehicleType;
import com.aufsell.Lab1.repository.FuelTypeRepository;
import com.aufsell.Lab1.repository.VehicleTypeRepository;
import com.aufsell.Lab1.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;
    @Autowired
    private FuelTypeRepository fuelTypeRepository;

    @GetMapping
    public ResponseEntity<List<VehicleDTO>> getAllVehicles() {
        List<VehicleDTO> vehicles = vehicleService.getAllVehicles();
        return ResponseEntity.status(HttpStatus.OK).body(vehicles);
    }

    @PostMapping
    public ResponseEntity<VehicleDTO> createVehicle(@RequestBody VehicleDTO vehicleDTO) {
        System.out.println(vehicleDTO);
        VehicleDTO createdVehicle = vehicleService.createVehicle(vehicleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVehicle);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDTO> getVehicleById(@PathVariable Long id) {
        VehicleDTO vehicleDTO = vehicleService.getVehicleById(id);
        return ResponseEntity.status(HttpStatus.OK).body(vehicleDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> updateVehicle(@PathVariable Long id, @Valid @RequestBody VehicleDTO vehicleDTO) {
        vehicleService.updateVehicle(id, vehicleDTO);
        return ResponseEntity.status(HttpStatus.OK).body(vehicleDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteVehicle(@PathVariable Long id, @RequestBody VehicleDeleteRequest request) {
        vehicleService.deleteVehicle(id, request.getUserId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/group-by-engine-power")
    public ResponseEntity<Map<Long,List<VehicleDTO>>> getVehiclesByEnginePower() {
        Map<Long,List<VehicleDTO>> vehicles = vehicleService.groupVehiclesByEnginePower();
        return ResponseEntity.status(HttpStatus.OK).body(vehicles);
    }

    @GetMapping("/count-by-fuel-consumption")
    public ResponseEntity<Long> countVehiclesByFuelConsumption(@RequestParam Long value) {
        long count = vehicleService.countVehiclesByFuelConsumptionLessThan(value);
        return ResponseEntity.status(HttpStatus.OK).body(count);
    }

    @GetMapping("/search")
    public ResponseEntity<List<VehicleDTO>> searchVehiclesByName(@RequestParam String nameSubstring) {
        List<VehicleDTO> vehicles = vehicleService.searchVehiclesByNameSubstring(nameSubstring);
        return ResponseEntity.status(HttpStatus.OK).body(vehicles);
    }

    @GetMapping("/type")
    public ResponseEntity<List<VehicleDTO>> getVehiclesByType(@RequestParam Long vehicleType) {
        List<VehicleDTO> vehicles = vehicleService.getVehiclesByType(vehicleType);
        return ResponseEntity.status(HttpStatus.OK).body(vehicles);
    }

    @PatchMapping("/{id}/add-wheels")
    public ResponseEntity<VehicleDTO> addWheels(@PathVariable Long id, @Valid @RequestParam int count) {
        VehicleDTO updatedVehicle = vehicleService.addWheels(id, count);
        return ResponseEntity.status(HttpStatus.OK).body(updatedVehicle);
    }

    @GetMapping("/vehicleTypes")
    @Operation(summary = "Получить список типов транспортных средств")
    public ResponseEntity<List<VehicleType>> getVehicleTypes() {
        List<VehicleType> vehicleType = vehicleTypeRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(vehicleType);
    }

    @GetMapping("/fuelTypes")
    @Operation(summary = "Получить список видов топлива")
    public ResponseEntity <List<FuelType>> getFuelTypes() {
        List<FuelType> fuelTypes = fuelTypeRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(fuelTypes);
    }
}
