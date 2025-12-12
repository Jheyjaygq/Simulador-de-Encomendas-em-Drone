package com.example.Simulador.de.Encomendas.em.Drone.Controller;
import com.example.Simulador.de.Encomendas.em.Drone.Dto.DroneOrdersDTO;
import com.example.Simulador.de.Encomendas.em.Drone.Service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/entregas")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    // Aciona o algoritmo de otimização
    @PostMapping
    public ResponseEntity<List<DroneOrdersDTO>> processarEntregas() {
        List<DroneOrdersDTO> resultado = deliveryService.processAllDeliveries();

        return ResponseEntity.ok(resultado);
    }
}
