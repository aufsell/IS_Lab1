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
    @NotNull
    private Long vehicleId;
    @NotNull
    private String name;
    private CoordinatesDTO coordinates;
    private Date creationDate;
    private VehicleTypeDTO vehicleType;
    @Positive
    private Long enginePower;
    @Positive
    private Long numberOfWheels;
    @Positive
    private Integer capacity;
    @Positive
    private Float distanceTravelled;
    @Positive
    private Double fuelConsumption;
    private FuelTypeDTO fuelType;
    @Positive
    private Long userID;
}
