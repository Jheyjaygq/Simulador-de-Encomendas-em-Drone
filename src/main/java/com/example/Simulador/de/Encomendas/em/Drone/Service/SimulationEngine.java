package com.example.Simulador.de.Encomendas.em.Drone.Service;


import com.example.Simulador.de.Encomendas.em.Drone.Entities.Drone;
import com.example.Simulador.de.Encomendas.em.Drone.Entities.DroneState;
import com.example.Simulador.de.Encomendas.em.Drone.Entities.Order;
import com.example.Simulador.de.Encomendas.em.Drone.Entities.OrderStatus;
import com.example.Simulador.de.Encomendas.em.Drone.Repositories.DroneRepository;
import com.example.Simulador.de.Encomendas.em.Drone.Repositories.OrderRepository;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.*;

@Service
public class SimulationEngine {


    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
    private final DroneRepository droneRepository;
    private final OrderRepository orderRepository;

    public SimulationEngine(DroneRepository droneRepository, OrderRepository orderRepository) {
        this.droneRepository = droneRepository;
        this.orderRepository = orderRepository;
    }


    private final double speedKmPerHour = 40.0;  // velocidade média do drone
    private final double weightFactorForBattery = 0.02; // impacto do peso no consumo
    private final double timeScale = 60.0; // 60 -> 1 minuto simulado = 1 segundo real


    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
    }

    public void dispatchRoute(Long droneId, List<Order> orders) {
        Drone drone = droneRepository.findById(droneId).orElseThrow();
        // define o drone status como crregando
        drone.setDroneState(DroneState.CARREGANDO);
        droneRepository.save(drone);



        // calcula pesos e distâncias
        double totalWeight = orders.stream().mapToDouble(o -> o.getWeightKg() == null ? 0.0 : o.getWeightKg()).sum();
        double routeKm = estimateRouteKm(orders);

        double consumption = routeKm * (1.0 + weightFactorForBattery * totalWeight);


        // tempo total de voo (horas) -> ms reais ajustados pelo timeScale
        double timeHours = routeKm / speedKmPerHour;
        long totalFlightMs = (long) ((timeHours * 3600_000) / timeScale);

        // tempo para carregar (pequeno delay) - em ms reais
        long loadMs = Math.max(100L, (long) (5_000 / timeScale)); // 5s simulado -> 5_000/timeScale real


        // agenda transição para EM_VOO após loadMs
        executor.schedule(() -> {
            Drone d = droneRepository.findById(droneId).orElseThrow();
            d.setDroneState(DroneState.EM_VOO);
            // decrementar bateria estimada (persistência)

            double newBattery = Math.max(0.0, d.getBatteryPercent() - consumption);
            d.setBatteryPercent(newBattery);
            droneRepository.save(d);


            // Estratégia simples: calcular tempo acumulado para cada parada (proporcional à distância)
            scheduleDeliveriesAndReturn(droneId, orders, totalFlightMs);

        }, loadMs, TimeUnit.MILLISECONDS);
    }

    private void scheduleDeliveriesAndReturn(Long droneId, List<Order> orders, long totalFlightMs) {
        if (orders == null || orders.isEmpty()) {



            executor.schedule(() -> {
                Drone d = droneRepository.findById(droneId).orElseThrow();
                d.setDroneState(DroneState.RETORNANDO);
                droneRepository.save(d);


                // pequeno delay de retorno
                executor.schedule(() -> {
                    Drone d2 = droneRepository.findById(droneId).orElseThrow();
                    d2.setDroneState(DroneState.IDLE);
                    droneRepository.save(d2);
                }, Math.max(50L, (long)(1000L / timeScale)), TimeUnit.MILLISECONDS);
            }, Math.max(50L, (long)(500L / timeScale)), TimeUnit.MILLISECONDS);
            return;
        }

        // calcular distâncias parciais para dividir o tempo do voo
        double baseX = 0, baseY = 0;
        double accumulatedKm = 0.0;
        // cria array com distâncias cumulativas desde base até cada parada
        double[] cumulativeKs = new double[orders.size()];
        double curX = baseX, curY = baseY;
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            double d = distance((int)curX, (int)curY, o.getDestinationX(), o.getDestinationY());
            accumulatedKm += d;
            cumulativeKs[i] = accumulatedKm;
            curX = o.getDestinationX(); curY = o.getDestinationY();
        }
        // adicionar trecho final de volta para base ao total (já incluso por quem calculou totalFlightMs)
        double totalRouteKm = cumulativeKs[cumulativeKs.length - 1] + distance((int)curX, (int)curY, 0, 0);

        // agendar entrega de cada pedido no instante correspondente
        for (int i = 0; i < orders.size(); i++) {
            double kmUntilThis = cumulativeKs[i];
            // fração do voo: kmUntilThis / totalRouteKm
            double fraction = totalRouteKm > 0 ? (kmUntilThis / totalRouteKm) : ((i+1) / (double)orders.size());
            long whenMs = (long) (totalFlightMs * fraction);

            final Order order = orders.get(i);
            final int index = i;
            executor.schedule(() -> {
                // marcar estado ENTREGANDO (pode ser rápido) — estamos ainda em voo mas entregando
                Drone d = droneRepository.findById(droneId).orElseThrow();
                d.setDroneState(DroneState.RETORNANDO);
                droneRepository.save(d);

                // atualizar pedido como ENTREGUE
                order.setOrderStatus(OrderStatus.ENTREGUE);
                orderRepository.save(order);


                // após pequena pausa, volta a EM_VOO se houver mais entregas pendentes
                executor.schedule(() -> {
                    Drone d2 = droneRepository.findById(droneId).orElseThrow();
                    // se ainda existem entregas futuras, volta para EM_VOO; caso contrário estado RETORNANDO será setado pela última tarefa
                    boolean moreToCome = index < (orders.size() - 1);
                    d2.setDroneState(moreToCome ? DroneState.EM_VOO : DroneState.RETORNANDO);
                    droneRepository.save(d2);
                }, Math.max(100L, (long)(1000L / timeScale)), TimeUnit.MILLISECONDS);

            }, whenMs, TimeUnit.MILLISECONDS);
        }

        // Agendar final (retorno ao hangar) um pouco depois do fim do voo (garante que últimas entregas foram processadas)
        executor.schedule(() -> {
            Drone d = droneRepository.findById(droneId).orElseThrow();
            d.setDroneState(DroneState.RETORNANDO);
            droneRepository.save(d);


            // simula tempo de retorno (pequeno)
            executor.schedule(() -> {
                Drone d2 = droneRepository.findById(droneId).orElseThrow();
                d2.setDroneState(DroneState.IDLE);
                droneRepository.save(d2);
            }, Math.max(200L, (long)(1500L / timeScale)), TimeUnit.MILLISECONDS);

        }, totalFlightMs + Math.max(50L, (long)(200L / timeScale)), TimeUnit.MILLISECONDS);
    }

    // estimador de rota: base (0,0) -> visit orders -> base (0,0)
    private double estimateRouteKm(List<Order> orders) {
        if (orders == null || orders.isEmpty()) return 0.0;
        double total = 0.0;
        int curX = 0, curY = 0;
        for (Order o : orders) {
            total += distance(curX, curY, o.getDestinationX(), o.getDestinationY());
            curX = o.getDestinationX(); curY = o.getDestinationY();
        }
        total += distance(curX, curY, 0, 0);
        return total;
    }

    private double distance(int x1, int y1, int x2, int y2) {
        return Math.hypot(x2 - x1, y2 - y1);
    }
}
