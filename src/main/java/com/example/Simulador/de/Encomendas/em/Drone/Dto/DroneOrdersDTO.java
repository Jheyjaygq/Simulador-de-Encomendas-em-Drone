package com.example.Simulador.de.Encomendas.em.Drone.Dto;

import com.example.Simulador.de.Encomendas.em.Drone.Entities.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DroneOrdersDTO {
    private Long droneId;
    private double droneCapacityKg;
    private double droneAutonomyKm;
    private String droneEstado;
    private List<Order> orders;
}
