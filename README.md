
# Simulador de Encomendas com Drones 

API REST desenvolvida em **Spring Boot** para gerenciar a logística de entrega de encomendas via drones, que permite o gerenciamento (Criar e ler) de Drones e Pedidos. 

## Sobre o Projeto

1.  **Drones** possuem especificações técnicas (capacidade de carga, autonomia, velocidade e bateria).
2.  **Pedidos** possuem peso, prioridade e coordenadas cartesianas (X, Y) simulando um mapa.
3.  O sistema aloca automaticamente os pedidos aos drones disponíveis usando uma lógica de otimização.
4.  Uma vez despachado, o drone entra em um ciclo de simulação de estados (`IDLE` ➝ `CARREGANDO` ➝ `EM_VOO` ➝ `ENTREGANDO` ➝ `RETORNANDO`).
5.  
---

## Tecnologias Utilizadas

  * **Linguagem:** Java (Versão 21)
  * **Framework Principal:** Spring Boot 4.0.0
  * **Banco de Dados:** H2 Database
  * **Build:** Apache Maven

-----

## Pré-requisitos e Instalação

Para compilar e executar este projeto, você precisará ter os seguintes softwares instalados em seu sistema:

1.  **Java Development Kit (JDK)**
    * Você pode baixar o JDK (por exemplo, OpenJDK) no site oficial da [Oracle](https://www.oracle.com/java/technologies/downloads/).

2.  **Apache Maven**
    * O Maven é usado para gerenciar as dependências e compilar o projeto. Você pode baixá-lo em [maven.apache.org](https://maven.apache.org/download.cgi).
    * *Nota: Muitas IDEs (como IntelliJ IDEA e Eclipse) já vêm com o Maven embutido.*



### Passos para Instalação

1.  **Clonar o repositório:**
    Abra seu terminal ou prompt de comando e execute o comando abaixo para baixar o código-fonte:

    ```bash
    git clone https:github.com/Jheyjaygq/Simulador-de-Encomendas-em-Drone
    ```

2.  **Navegar para o diretório do projeto:**

    ```bash
    cd Simulador-de-Encomendas-em-Drone
    ```

3.  **Baixar dependências e compilar:**
    Execute o comando do Maven para baixar todas as dependências e compilar o projeto:

    ```bash
    mvn clean install
    ```
4.  **Execute a aplicação:**
    ```bash
    mvn spring-boot:run
    ```

5.  **Acesse a API:**
    * URL Base: `http://localhost:8080`

6.  **Acesse o Console do Banco (H2):**
    * URL: `http://localhost:8080/h2-console`
    * **JDBC URL:** `jdbc:h2:mem:dronedb`
    * **User:** `sa`
    * **Password:** `password`


-----

##  Documentação da API (Endpoints)


**Cadastrar um Drone**

`POST /api/drones`

```json
{
  "capacityKg": 15.0,
  "autonomyKm": 50.0,
  "batteryPercent": 100.0,
  "speedKmPerHour": 40.0
}

**Listar Drones**

`GET /api/drones`

```json recebido
[
    {
        "id": 1,
        "capacityKg": 15.0,
        "autonomyKm": 50.0,
        "batteryPercent": 100.0,
        "speedKmPerHour": 40.0,
        "droneState": "IDLE"
    },
    {
        "id": 2,
        "capacityKg": 15.0,
        "autonomyKm": 50.0,
        "batteryPercent": 100.0,
        "speedKmPerHour": 40.0,
        "droneState": "IDLE"
    }
]


**Cadastrar um Pedido**

`POST /api/pedidos`

```json
{
    "destinationX": 20,
    "destinationY": 50,
    "weightKg": 34,
    "priority": "BAIXA"
}
**Listar Pedidos**

`GET /api/pedidos`

```json recebido
[
    {
        "id": 1,
        "destinationX": 20,
        "destinationY": 50,
        "weightKg": 34.0,
        "priority": "BAIXA",
        "orderStatus": "PENDENTE",
        "dateCreation": "2025-12-12T09:08:37.872946"
    },
    {
        "id": 2,
        "destinationX": 12,
        "destinationY": 12,
        "weightKg": 20.0,
        "priority": "ALTA",
        "orderStatus": "PENDENTE",
        "dateCreation": "2025-12-12T09:10:07.569198"
    },
    {
        "id": 3,
        "destinationX": 5,
        "destinationY": 12,
        "weightKg": 20.0,
        "priority": "ALTA",
        "orderStatus": "PENDENTE",
        "dateCreation": "2025-12-12T09:10:16.512368"
    }
]

**Disparar Entregas (Otimização)**

`POST /api/entregas`

* Este endpoint não requer corpo (body). Ele aciona o DeliveryService que executa a seguinte lógica:

* Busca pedidos com status PENDENTE (ordenados por prioridade).

* Busca drones com status IDLE (ordenados por maior capacidade).

* Verifica se o drone suporta o peso total e se tem bateria para a distância (ida + volta).

* Inicia a Simulação Assíncrona e retorna o plano de voo gerado.


```json recebido

[
    {
        "droneId": 4,
        "droneCapacityKg": 100.0,
        "droneAutonomyKm": 90.0,
        "droneEstado": "CARREGANDO",
        "orders": [
            {
                "id": 2,
                "destinationX": 12,
                "destinationY": 12,
                "weightKg": 20.0,
                "priority": "ALTA",
                "orderStatus": "ALOCADO",
                "dateCreation": "2025-12-12T09:10:07.569198"
            },
            {
                "id": 3,
                "destinationX": 5,
                "destinationY": 12,
                "weightKg": 20.0,
                "priority": "ALTA",
                "orderStatus": "ALOCADO",
                "dateCreation": "2025-12-12T09:10:16.512368"
            },
            {
                "id": 4,
                "destinationX": 5,
                "destinationY": 12,
                "weightKg": 20.0,
                "priority": "ALTA",
                "orderStatus": "ALOCADO",
                "dateCreation": "2025-12-12T09:15:15.832376"
            }
        ]
    }
]
-----

## Estrutura de Pastas

com.example.Simulador...
├── Controller       # Camada REST (Endpoints)
├── Dto              # Objetos de Transferência (DTOs)
├── Entities         # Entidades JPA (Drone, Order, Enums)
├── Repositories     # Acesso a Dados (Interfaces JPA)
└── Service          # Regras de Negócio

-----
  
