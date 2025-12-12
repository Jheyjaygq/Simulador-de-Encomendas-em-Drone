package com.example.Simulador.de.Encomendas.em.Drone.Service;

import com.example.Simulador.de.Encomendas.em.Drone.Dto.DroneOrdersDTO;
import com.example.Simulador.de.Encomendas.em.Drone.Entities.Drone;
import com.example.Simulador.de.Encomendas.em.Drone.Entities.DroneState;
import com.example.Simulador.de.Encomendas.em.Drone.Entities.Order;
import com.example.Simulador.de.Encomendas.em.Drone.Entities.OrderStatus;
import com.example.Simulador.de.Encomendas.em.Drone.Repositories.DroneRepository;
import com.example.Simulador.de.Encomendas.em.Drone.Repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DeliveryService {

    private final OrderRepository orderRepository;

    private final DroneRepository droneRepository;

    private final SimulationEngine simulationEngine;

    public DeliveryService(OrderRepository orderRepository,
                           DroneRepository droneRepository,
                           SimulationEngine simulationEngine){
        this.droneRepository = droneRepository;
        this.orderRepository = orderRepository;
        this.simulationEngine = simulationEngine;
    }





    public List<DroneOrdersDTO> processAllDeliveries() {

        // busca os drones disponiveis , que possuem o estado idle
        List<Drone> dronesAvailable = Optional.ofNullable(droneRepository.findByDroneState(DroneState.IDLE))
                .orElse(Collections.emptyList());

        // busca os pedidos pendentes
        List<Order> ordersPending = Optional.ofNullable(orderRepository.findByOrderStatus(OrderStatus.PENDENTE))
                .orElse(new ArrayList<>());

        // ordena os pedidos por maior prioridade
        ordersPending.sort(Comparator.comparing(Order::getPriority).reversed());

        // ordena os drones com mais capacidade de carga
        dronesAvailable.sort(Comparator.comparing(Drone::getCapacityKg).reversed());


        Map<Long, List<Order>> alocacaoResultados = new HashMap<>();

        for (Drone drone : dronesAvailable) {
            List<Order> cargaDoDrone = new ArrayList<>();


            double pesoAtual = 0;
            double distanciaTotalSimulada = 0;
            int xAtual = 0, yAtual = 0;

            Iterator<Order> iteradorPedidos = ordersPending.iterator();
            while (iteradorPedidos.hasNext()) {
                Order p = iteradorPedidos.next();
                if (p.getWeightKg() == null) continue; // defensivo

                double pesoTentativa = pesoAtual + p.getWeightKg();

                if (pesoTentativa <= drone.getCapacityKg()) {
                    double distParaPedido = calculateDistance(xAtual, yAtual, p.getDestinationX(), p.getDestinationY());
                    double distRetornoDaViagem = calculateDistance(p.getDestinationX(), p.getDestinationY(), 0, 0);
                    double distanciaTentativa = distanciaTotalSimulada + distParaPedido + distRetornoDaViagem;

                    if (distanciaTentativa <= drone.getAutonomyKm()) {
                        cargaDoDrone.add(p);
                        pesoAtual = pesoTentativa;
                        distanciaTotalSimulada += distParaPedido;
                        xAtual = p.getDestinationX();
                        yAtual = p.getDestinationY();
                        iteradorPedidos.remove();
                    }
                }
            }

            if (!cargaDoDrone.isEmpty()) {
                alocacaoResultados.put(drone.getId(), cargaDoDrone);

                // atualiza estado e persiste
                drone.setDroneState(DroneState.CARREGANDO); // primeiro carregando
                droneRepository.save(drone);

                // marca pedidos como ALOCADO
                cargaDoDrone.forEach(pedido -> {
                    pedido.setOrderStatus(OrderStatus.ALOCADO);
                    orderRepository.save(pedido);
                });

                // dispara a simulação assincrona (no SimulationEngine você mudará pra EM_VOO depois do load delay)
                simulationEngine.dispatchRoute(drone.getId(), cargaDoDrone);
            }

        }

        // === Converte o Map<Long, List<Order>> para List<DroneOrdersDTO> ===
        List<DroneOrdersDTO> dtos = new ArrayList<>();
        for (Map.Entry<Long, List<Order>> entry : alocacaoResultados.entrySet()) {
            Long droneId = entry.getKey();
            List<Order> orders = entry.getValue();

            // recupera dados do drone para preencher o DTO
            Optional<Drone> optDrone = droneRepository.findById(droneId);
            if (optDrone.isPresent()) {
                Drone d = optDrone.get();
                DroneOrdersDTO dto = new DroneOrdersDTO(
                        d.getId(),
                        d.getCapacityKg(),
                        d.getAutonomyKm(),
                        d.getDroneState() != null ? d.getDroneState().name() : null,
                        orders
                );
                dtos.add(dto);
            } else {
                // se não encontrou o drone, ainda adiciona com campos mínimos
                DroneOrdersDTO dto = new DroneOrdersDTO(
                        droneId,
                        0.0,
                        0.0,
                        "UNKNOWN",
                        orders
                );
                dtos.add(dto);
            }
        }

        return dtos;
    }


    private double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
