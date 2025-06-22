package com.tienda.productos.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void categoriaDTO_deberiaTenerViolacionSiIdEsNulo() {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(null);

        Set<ConstraintViolation<CategoryDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        ConstraintViolation<CategoryDTO> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("id");
        assertThat(violation.getMessage()).isEqualTo("El ID de la categoría es obligatorio");
    }

    @Test
    void categoriaDTO_deberiaSerValidoConId() {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(1L);

        Set<ConstraintViolation<CategoryDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
