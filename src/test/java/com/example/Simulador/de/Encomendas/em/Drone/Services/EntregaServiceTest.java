package com.example.Simulador.de.Encomendas.em.Drone.Services;

import com.example.Simulador.de.Encomendas.em.Drone.Entities.Drone;
import com.example.Simulador.de.Encomendas.em.Drone.Entities.DroneState;
import com.example.Simulador.de.Encomendas.em.Drone.Entities.Order;
import com.example.Simulador.de.Encomendas.em.Drone.Entities.OrderStatus;
import com.example.Simulador.de.Encomendas.em.Drone.Entities.Priority;
import com.example.Simulador.de.Encomendas.em.Drone.Repositories.DroneRepository;
import com.example.Simulador.de.Encomendas.em.Drone.Repositories.OrderRepository;
import com.example.Simulador.de.Encomendas.em.Drone.Service.DeliveryService;
import com.example.Simulador.de.Encomendas.em.Drone.Service.SimulationEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EntregaServiceTest {

    @Mock
    private DroneRepository droneRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SimulationEngine simulationEngine; // <-- importante

    @InjectMocks
    private DeliveryService deliveryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAllocateHighPriorityOrdersFirst() {

        Drone drone = new Drone();
        drone.setId(1L);
        drone.setCapacityKg(20.0);
        drone.setAutonomyKm(200.0);
        drone.setDroneState(DroneState.IDLE);

        Order high = new Order();
        high.setId(1L);
        high.setWeightKg(5.0);
        high.setPriority(Priority.ALTA);
        high.setOrderStatus(OrderStatus.PENDENTE);
        high.setDestinationX(1);
        high.setDestinationY(1);

        Order medium = new Order();
        medium.setId(2L);
        medium.setWeightKg(5.0);
        medium.setPriority(Priority.MEDIA);
        medium.setOrderStatus(OrderStatus.PENDENTE);
        medium.setDestinationX(2);
        medium.setDestinationY(2);

        // listas mutáveis para evitar UnsupportedOperationException
        when(droneRepository.findByDroneState(DroneState.IDLE))
                .thenReturn(new ArrayList<>(List.of(drone)));

        when(orderRepository.findByOrderStatus(OrderStatus.PENDENTE))
                .thenReturn(new ArrayList<>(List.of(high, medium)));

        // garantir que a chamada a dispatchRoute não faça nada no teste
        doNothing().when(simulationEngine).dispatchRoute(anyLong(), anyList());

        List<?> resultado = deliveryService.processAllDeliveries();

        // validações
        assertEquals(OrderStatus.ALOCADO, high.getOrderStatus(), "Pedido de alta prioridade deveria ser ALOCADO");
        verify(orderRepository, atLeastOnce()).save(high);
        verify(simulationEngine, atLeastOnce()).dispatchRoute(anyLong(), anyList());
        assertFalse(resultado.isEmpty(), "Resultado da alocação não deve ser vazio");
    }
}
