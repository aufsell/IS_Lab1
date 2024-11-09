package com.aufsell.Lab1.controller;

import com.aufsell.Lab1.dto.VehicleUpdateMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class VehicleWebSocketController {
    @MessageMapping("/vehicles")
    @SendTo("/topic/vehicles")
    public VehicleUpdateMessage handleVehicleUpdate (VehicleUpdateMessage message){
        System.out.println("gresh" + message);
        return message;
    }
}
