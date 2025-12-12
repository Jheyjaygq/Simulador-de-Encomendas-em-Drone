package com.example.Simulador.de.Encomendas.em.Drone.Services;

import com.example.Simulador.de.Encomendas.em.Drone.Entities.Order;
import com.example.Simulador.de.Encomendas.em.Drone.Entities.Priority;
import com.example.Simulador.de.Encomendas.em.Drone.Repositories.OrderRepository;
import com.example.Simulador.de.Encomendas.em.Drone.Service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    public OrderServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateOrderAsPendente() {
        Order order = new Order();
        order.setWeightKg(5.0);
        order.setPriority(Priority.ALTA);

        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.save(order);

        assertEquals("PENDENTE", result.getOrderStatus().toString());
        assertNotNull(result.getDateCreation());
    }
}
