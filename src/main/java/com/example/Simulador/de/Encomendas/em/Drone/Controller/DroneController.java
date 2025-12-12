package com.example.Simulador.de.Encomendas.em.Drone.Controller;

import com.example.Simulador.de.Encomendas.em.Drone.Entities.Drone;
import com.example.Simulador.de.Encomendas.em.Drone.Service.DroneService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drones")
public class DroneController {

    private final DroneService droneService;

    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }

 // Listar todos os drones cadastrados
    @GetMapping
    public ResponseEntity<List<Drone>> listarDrones() {
        List<Drone> drones = droneService.findAll();
        if (drones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(drones);
    }

    // Criar um novo drone
    @PostMapping
    public ResponseEntity<Drone> criarDrone(@Valid @RequestBody Drone drone) {
        Drone novoDrone = droneService.save(drone);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoDrone);
    }
}