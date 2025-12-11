package com.example.Simulador.de.Encomendas.em.Drone.Repositories;

import com.example.Simulador.de.Encomendas.em.Drone.Entities.Drone;
import com.example.Simulador.de.Encomendas.em.Drone.Entities.DroneState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DroneRepository extends JpaRepository<Drone, Long> {

    List<Drone> findByDroneState(DroneState droneState);
}
