package com.aufsell.Lab1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Name cannot be null")
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Coordinates cannot be null")
    @ManyToOne
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @ManyToOne
    @NotNull(message = "Vehicle type cannot be null")
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private VehicleType vehicleType;

    @Positive(message = "Engine power must be a positive number")
    @NotNull(message = "Engine power cannot be null")
    @Column
    private Long enginePower;

    @Positive(message = "Number of wheels must be greater than 0")
    @NotNull(message = "Number of wheels cannot be null")
    @Column(nullable = false)
    private long numberOfWheels;

    @NotNull(message = "Capacity cannot be null")
    @Positive(message = "Capacity must be greater than 0")
    @Column(nullable = false)
    private Integer capacity;

    @Positive(message = "Distance travelled must be greater than 0")
    @NotNull(message = "Distance travelled cannot be null")
    @Column(nullable = false)
    private float distanceTravelled;

    @Positive(message = "Fuel consumption must be greater than 0")
    @NotNull(message = "Fuel consumption cannot be null")
    @Column(nullable = false)
    private double fuelConsumption;

    @ManyToOne
    @NotNull(message = "Fuel type cannot be null")
    @JoinColumn(name = "fuel_type_id", nullable = false)
    private FuelType fuelType;

    @ManyToOne
    @NotNull(message = "User cannot be null")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        this.creationDate = new Date();
    }
}
