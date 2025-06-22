package com.tienda.productos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tienda.productos.ProductosApplication;
import com.tienda.productos.dto.ProductDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ProductosApplication.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void flujoCompleto_crearYObtenerProducto() throws Exception {
        ProductDTO nuevoProducto = new ProductDTO();
        nuevoProducto.setNombre("Monitor");
        nuevoProducto.setDescripcion("Monitor 4K");
        nuevoProducto.setPrecio(400.0);
        nuevoProducto.setStock(5);
        nuevoProducto.setCategoriaId(1L);
        nuevoProducto.setImagenUrl("http://imagen.com/monitor");

        // Crear producto
        String response = mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoProducto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ProductDTO creado = objectMapper.readValue(response, ProductDTO.class);

        // Obtener producto
        mockMvc.perform(get("/api/productos/{id}", creado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Monitor"));

        // Actualizar producto
        creado.setPrecio(450.0);
        creado.setStock(8);

        mockMvc.perform(put("/api/productos/{id}", creado.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precio").value(450.0))
                .andExpect(jsonPath("$.stock").value(8));

        // Eliminar producto
        mockMvc.perform(delete("/api/productos/{id}", creado.getId()))
                .andExpect(status().isNoContent());

        // Verificar que ya no existe
        mockMvc.perform(get("/api/productos/{id}", creado.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void flujoNegativo_crearActualizarEliminarConErrores() throws Exception {
        // 1. Crear con datos inválidos
        ProductDTO invalido = new ProductDTO();
        invalido.setNombre(""); // nombre vacío
        invalido.setPrecio(-10.0); // precio negativo
        invalido.setStock(-5); // stock negativo
        invalido.setCategoriaId(null); // categoría nula

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());

        // 2. Obtener producto inexistente
        mockMvc.perform(get("/api/productos/{id}", 99999L))
                .andExpect(status().isNotFound());

        // 3. Actualizar producto inexistente
        ProductDTO inexistente = new ProductDTO();
        inexistente.setId(99999L);
        inexistente.setNombre("Falso");
        inexistente.setDescripcion("No existe");
        inexistente.setPrecio(100.0);
        inexistente.setStock(1);
        inexistente.setCategoriaId(1L);

        mockMvc.perform(put("/api/productos/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inexistente)))
                .andExpect(status().isNotFound());

        // 4. Eliminar producto inexistente
        mockMvc.perform(delete("/api/productos/{id}", 99999L))
                .andExpect(status().isNotFound());
    }
}
