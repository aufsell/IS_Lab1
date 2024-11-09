package com.aufsell.Lab1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VehicleUpdateMessage {
    private String activity;
    private Long vehicleId;
    private VehicleDTO vehicle;
}
