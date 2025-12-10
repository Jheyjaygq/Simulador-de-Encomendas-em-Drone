package com.example.Simulador.de.Encomendas.em.Drone.Entities;

import jakarta.persistence.*;
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

    private Double capacitykg; // capacidade de kg suportadas pelo drone
    private Double autonomykm;  // capacidade de kl suportadas pelo dronw

    // Status para simulação (Idle, Carregando, Entregando) - Diferencial [cite: 272]
    @Enumerated(EnumType.STRING)
    private EstadoDrone estado = EstadoDrone.IDLE;
}

enum EstadoDrone { IDLE, CARREGANDO, EM_VOO, RETORNANDO }