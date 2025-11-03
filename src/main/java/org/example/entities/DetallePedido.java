package org.example.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("detalle_pedidos")
public class DetallePedido {
    @Id
    private long id;

    private Long pedidoId;
    private Long productoId;
    private Integer cantidad;
    private Double precioUnitario;
}
