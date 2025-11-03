package org.example.dto;

import lombok.Data;
import org.example.dto.DetallePedidoDTO;

import java.util.List;
//Para peticiones de pedidos
@Data
public class PedidoRequestDTO {
    private String cliente;
    private List<DetallePedidoDTO> detalles;
}
