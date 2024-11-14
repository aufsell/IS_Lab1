package com.aufsell.Lab1.dto;

import com.aufsell.Lab1.model.FuelType;
import com.aufsell.Lab1.model.User;
import com.aufsell.Lab1.model.VehicleType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Date;

@Data
public class VehicleDTO {
    private Long vehicleId;
    @NotNull(message = "name not be null")
    private String name;
    private CoordinatesDTO coordinates;
    private Date creationDate;
    private VehicleTypeDTO vehicleType;
    @NotNull(message = "enginePower not be null")
    @Positive
    private Long enginePower;
    @NotNull(message = "numberOfWheels not be null")
    @Positive
    private Long numberOfWheels;
    @NotNull(message = "capacity not be null")
    @Positive
    private Integer capacity;
    @NotNull(message = "distanceTravelled not be null")
    @Positive
    private Float distanceTravelled;
    @NotNull(message = "fuelConsumption not be null")
    @Positive
    private Double fuelConsumption;
    @NotNull(message = "fuelType not be null")
    private FuelTypeDTO fuelType;
    @Positive
    private Long userID;
}
