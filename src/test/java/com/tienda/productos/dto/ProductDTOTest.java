package com.tienda.productos.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ProductDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void nombre_deberiaSerInvalido_siEsNull() {
        ProductDTO dto = new ProductDTO();
        dto.setNombre(null);  // Restricción @NotBlank

        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Se esperaba una violación por nombre nulo");

        boolean contieneErrorNombre = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre"));
        assertTrue(contieneErrorNombre, "Se esperaba una violación específicamente en el campo 'nombre'");
    }

    @Test
    void nombre_deberiaSerInvalido_siEsVacio() {
        ProductDTO dto = new ProductDTO();
        dto.setNombre("   ");  // Cadena en blanco

        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Se esperaba una violación por nombre vacío");
    }

    @Test
    void dtoValido_noDebeTenerViolaciones() {
        ProductDTO dto = new ProductDTO();
        dto.setId(1L);
        dto.setNombre("Laptop");
        dto.setDescripcion("Laptop potente");
        dto.setPrecio(999.99);
        dto.setStock(10);
        dto.setCategoriaId(2L);
        dto.setImagenUrl("http://ejemplo.com/laptop.png");

        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "No se esperaban violaciones para un DTO válido");
    }

    @Test
    void nombreValido_noDebeGenerarErrores() {
        ProductDTO dto = new ProductDTO(1L, "Producto A", "Desc", 10.0, 5, 2L, "http://imagen.com");
        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "No debe haber errores de validación");
    }

    @Test
    void nombreNull_debeGenerarError() {
        ProductDTO dto = new ProductDTO(1L, null, "Desc", 10.0, 5, 2L, "http://imagen.com");
        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("El nombre del producto es obligatorio", violations.iterator().next().getMessage());
    }

    @Test
    void nombreVacio_debeGenerarError() {
        ProductDTO dto = new ProductDTO(1L, "", "Desc", 10.0, 5, 2L, "http://imagen.com");
        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("El nombre del producto es obligatorio", violations.iterator().next().getMessage());
    }

    @Test
    void nombreSoloEspacios_debeGenerarError() {
        ProductDTO dto = new ProductDTO(1L, "   ", "Desc", 10.0, 5, 2L, "http://imagen.com");
        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("El nombre del producto es obligatorio", violations.iterator().next().getMessage());
    }
}
