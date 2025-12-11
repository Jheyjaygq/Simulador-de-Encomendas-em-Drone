package com.example.Simulador.de.Encomendas.em.Drone.Repositories;

import com.example.Simulador.de.Encomendas.em.Drone.Entities.Order;
import com.example.Simulador.de.Encomendas.em.Drone.Entities.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByOrderStatus(OrderStatus status);
}
