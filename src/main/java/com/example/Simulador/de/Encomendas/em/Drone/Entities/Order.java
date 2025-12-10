package com.example.Simulador.de.Encomendas.em.Drone.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer destinationX; // Localização X
    private Integer destinationY; // Localização Y
    private Double weightKg; // peso do pedido

    @Enumerated(EnumType.STRING)
    private Priority priority; // Alta, Média, Baixa

    private OrderStatus orderStatus;
    private LocalDateTime dateCreation = LocalDateTime.now();
}

enum Priority { ALTA, MEDIA, BAIXA }
// StatusPedido
enum OrderStatus { PENDENTE, ALOCADO, ENTREGUE, CANCELADO }
// Adicione o campo: private StatusPedido status = StatusPedido.PENDENTE;