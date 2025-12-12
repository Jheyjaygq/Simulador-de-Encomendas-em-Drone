package com.example.Simulador.de.Encomendas.em.Drone.Service;

import com.example.Simulador.de.Encomendas.em.Drone.Entities.Order;
import com.example.Simulador.de.Encomendas.em.Drone.Entities.OrderStatus;
import com.example.Simulador.de.Encomendas.em.Drone.Repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order save(Order order) {
        if (order.getOrderStatus() == null) {
            order.setOrderStatus(OrderStatus.PENDENTE);
        }
        return orderRepository.save(order);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
