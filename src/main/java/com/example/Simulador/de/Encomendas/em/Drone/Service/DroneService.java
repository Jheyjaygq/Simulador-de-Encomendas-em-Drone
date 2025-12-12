package com.example.Simulador.de.Encomendas.em.Drone.Service;

import com.example.Simulador.de.Encomendas.em.Drone.Entities.Drone;
import com.example.Simulador.de.Encomendas.em.Drone.Entities.DroneState;
import com.example.Simulador.de.Encomendas.em.Drone.Repositories.DroneRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DroneService {

    private final DroneRepository droneRepository;

    public DroneService(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }

    public Drone save(Drone drone) {
        if (drone.getDroneState() == null) {
            drone.setDroneState(DroneState.IDLE);
        }
        return droneRepository.save(drone);
    }

    public List<Drone> findAll() {
        return droneRepository.findAll();
    }

    public Optional<Drone> findById(long l) {
        return droneRepository.findById(l);
    }
}