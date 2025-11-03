package org.example.dto;

import lombok.Data;

//Para el tratamiento de la api de producto
@Data
public class ProductoDTO {
    private Long id;

    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private Boolean activo;
}
