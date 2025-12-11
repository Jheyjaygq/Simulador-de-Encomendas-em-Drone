package com.example.Simulador.de.Encomendas.em.Drone.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer destinationX; // Localização X

    @NotNull
    private Integer destinationY; // Localização Y

    @NotNull
    private Double weightKg; // peso do pedido

    @Enumerated(EnumType.STRING)
    private Priority priority; // Alta, Média, Baixa

    private OrderStatus orderStatus  = OrderStatus.PENDENTE;

    private LocalDateTime dateCreation = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
    }
}

