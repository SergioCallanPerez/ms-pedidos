package org.example.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

//Para peticiones de pedidos
@Data
public class PedidoResponseDTO {
    private Long id;
    private String cliente;
    private LocalDateTime fecha;
    private Double total;
    private String estado;
    private List<DetallePedidoDTO> detalles;
}
