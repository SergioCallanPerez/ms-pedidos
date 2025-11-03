package org.example.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("pedidos")
public class Pedido {
    @Id
    private Long id;

    private String cliente;
    private LocalDateTime fecha;
    private Double total;
    private String estado; // PENDIENTE, PROCESADO, CANCELADO
}
