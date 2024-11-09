package com.aufsell.Lab1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Не может быть null")
    @NotBlank(message = "Не может быть пустым")
    private String name;

    @NotNull(message = "Не может быть null")
    @ManyToOne
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @ManyToOne
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private VehicleType vehicleType;

    @Positive(message = "Должно быть положительным")
    @Column
    private Long enginePower;

    @Positive(message = "Number of wheels must be greater than 0")
    @Column(nullable = false)
    private long numberOfWheels;

    @NotNull(message = "Не может быть null")
    @Positive(message = "Должно быть больше 0")
    @Column(nullable = false)
    private Integer capacity;

    @Positive(message = "Должно быть больше 0")
    @Column(nullable = false)
    private float distanceTravelled;

    @Positive(message = "Должно быть больше 0")
    @Column(nullable = false)
    private double fuelConsumption;

    @ManyToOne
    @JoinColumn(name = "fuel_type_id", nullable = false)
    private FuelType fuelType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        this.creationDate = new Date();
    }
}
