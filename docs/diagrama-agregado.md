graph TD
%% Límite del Agregado Pedido
subgraph Límite del Agregado: Pedido
R[**Pedido** <br> Raíz de Agregado]
D[**DetallePedido** <br> Entidad Local]
E[**EstadoPedido** <br> Value Object]

        R -- Contiene (1..N) --> D
        R -- Define estado --> E
    end

    %% Elementos Fuera del Límite (Relaciones por ID)
    subgraph Fuera del Límite
        C[**Cliente** <br> Raíz de Agregado]
        A[**ArticuloKit / Ejemplar** <br> Raíz de Agregado]
    end

    %% Relaciones cruzando el límite
    R -. Referencia por idCliente .-> C
    D -. Referencia por idArticulo .-> A

    %% Estilos visuales
    style R fill:#d4edda,stroke:#28a745,stroke-width:2px
    style D fill:#e2e3e5,stroke:#383d41
    style E fill:#e2e3e5,stroke:#383d41
    style C fill:#f8d7da,stroke:#dc3545,stroke-width:2px,stroke-dasharray: 5 5
    style A fill:#f8d7da,stroke:#dc3545,stroke-width:2px,stroke-dasharray: 5 5