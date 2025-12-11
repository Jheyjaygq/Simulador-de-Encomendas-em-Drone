package com.example.Simulador.de.Encomendas.em.Drone.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Entity
@NoArgsConstructor
@Data
public class Drone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private double capacityKg; // capacidade de kg suportadas pelo drone

    @NotNull
    private double autonomyKm;  // capacidade de kl suportadas pelo dronw

    private Double batteryPercent = 100.0; // porcentagem da bateria

    private Double speedKmPerHour = 40.0;


    @Enumerated(EnumType.STRING) // salva como string no db
    private DroneState droneState; // status do drone "IDLE, CARREGANDO, EM_VOO, ENTREGANDO, RETORNANDO"

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (droneState == null) {
            droneState = DroneState.IDLE;
        }
    }
}

