package com.example.Simulador.de.Encomendas.em.Drone.Services;

import com.example.Simulador.de.Encomendas.em.Drone.Entities.Drone;
import com.example.Simulador.de.Encomendas.em.Drone.Repositories.DroneRepository;
import com.example.Simulador.de.Encomendas.em.Drone.Service.DroneService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DroneServiceTest {

    @Mock
    private DroneRepository droneRepository;

    @InjectMocks
    private DroneService droneService;

    public DroneServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateDroneWithIdleStatus() {
        Drone drone = new Drone();
        drone.setCapacityKg(10);
        drone.setAutonomyKm(50);
        drone.setBatteryPercent(100.0);

        when(droneRepository.save(any())).thenAnswer(i -> {
            Drone d = i.getArgument(0);
            d.setId(1L);
            return d;
        });

        Drone result = droneService.save(drone);

        assertEquals("IDLE", result.getDroneState().toString());
        assertNotNull(result.getId());
    }

    @Test
    void shouldFindDroneById() {
        Drone drone = new Drone();
        drone.setId(1L);

        when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));

        Optional<Drone> found = droneService.findById(1L);

        assertEquals(1L, found.get().getId());
    }
}
