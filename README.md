# 🚁 Simulador de Encomendas com Drones (Spring Boot)

API REST desenvolvida em **Spring Boot** para gerenciar a logística de entrega de encomendas via drones. O sistema não apenas gerencia o CRUD de pedidos e drones, mas possui um **Motor de Simulação (Simulation Engine)** que realiza o despacho, controla o consumo de bateria e simula o tempo de voo em tempo real de forma assíncrona.

## 📋 Sobre o Projeto

Este projeto simula um sistema de entregas autônomas onde:

1.  **Drones** possuem especificações técnicas (capacidade de carga, autonomia, velocidade e bateria).
2.  **Pedidos** possuem peso, prioridade e coordenadas cartesianas (X, Y) simulando um mapa.
3.  O sistema aloca automaticamente os pedidos aos drones disponíveis usando uma lógica de otimização.
4.  Uma vez despachado, o drone entra em um ciclo de simulação de estados (`IDLE` ➝ `CARREGANDO` ➝ `EM_VOO` ➝ `ENTREGANDO` ➝ `RETORNANDO`).

## 🛠️ Tecnologias Utilizadas

* **Java 21**
* **Spring Boot 3** (Web, Data JPA)
* **H2 Database** (Banco de dados em memória para execução rápida)
* **Lombok** (Redução de código boilerplate)

---

## 🚀 Como Executar o Projeto

### Pré-requisitos

* Java JDK 17 ou superior.
* Maven instalado.

### Passos

1.  **Clone o repositório:**
    ```bash
    git clone [https:github.com/Jheyjaygq/Simulador-de-Encomendas-em-Drone](github.com/Jheyjaygq/Simulador-de-Encomendas-em-Drone)
    cd seu-repo
    ```

2.  **Execute a aplicação:**
    ```bash
    mvn spring-boot:run
    ```

3.  **Acesse a API:**
    * URL Base: `http://localhost:8080`

4.  **Acesse o Console do Banco (H2):**
    * URL: `http://localhost:8080/h2-console`
    * **JDBC URL:** `jdbc:h2:mem:dronedb`
    * **User:** `sa`
    * **Password:** `password`

---

## 📡 Documentação da API (Endpoints)

### 1. 🛸 Drones

**Cadastrar um Drone**
`POST /api/drones`

```json
{
  "capacityKg": 15.0,
  "autonomyKm": 100.0,
  "speedKmPerHour": 60.0
}
