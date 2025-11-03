package org.example.dto;

import lombok.Data;

//Para el manejo de actualizarStock (Producto)
@Data
public class DetallePedidoDTO {
    private Long productoId;
    private Integer cantidad;
}
