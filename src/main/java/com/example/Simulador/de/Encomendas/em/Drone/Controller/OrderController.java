package com.example.Simulador.de.Encomendas.em.Drone.Controller;

import com.example.Simulador.de.Encomendas.em.Drone.Entities.Order;
import com.example.Simulador.de.Encomendas.em.Drone.Service.OrderService; // Você precisará deste serviço
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // cadastra pedido
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        Order novoPedido = orderService.save(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
    }
    // lista todos os pedidos
    @GetMapping
    public ResponseEntity<List<Order>> listOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }
}