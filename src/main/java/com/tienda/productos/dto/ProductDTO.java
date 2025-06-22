package com.tienda.productos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;
    
    private String descripcion;
    private Double precio;
    private Integer stock;
    private Long categoriaId;
    private String imagenUrl;
}
