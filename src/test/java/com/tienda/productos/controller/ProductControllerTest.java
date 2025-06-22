package com.tienda.productos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tienda.productos.dto.ProductDTO;
import com.tienda.productos.service.ProductService;
import com.tienda.productos.model.OrderItem;
import com.tienda.productos.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllProducts_deberiaRetornarListaDeProductos() throws Exception {
        ProductDTO producto1 = new ProductDTO(1L, "Producto 1", "Desc 1", 10.0, 5, 1L, "img1.jpg");
        ProductDTO producto2 = new ProductDTO(2L, "Producto 2", "Desc 2", 15.0, 10, 2L, "img2.jpg");

        when(productService.getAllProducts()).thenReturn(List.of(producto1, producto2));

        mockMvc.perform(get("/api/productos/all")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$[0].nombre").value("Producto 1"))
            .andExpect(jsonPath("$[1].nombre").value("Producto 2"));
    }

    @Test
    void getProductById_deberiaRetornarProductoSiExiste() throws Exception {
    ProductDTO productDTO = new ProductDTO(1L, "Producto 1", "Descripción", 99.99, 10, 2L, "url");
    when(productService.getProductById(1L)).thenReturn(productDTO);

    mockMvc.perform(get("/api/productos/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.nombre").value("Producto 1"))
        .andExpect(jsonPath("$.precio").value(99.99));
    }

    @Test
    void getProductById_deberiaRetornar404SiProductoNoExiste() throws Exception {
    when(productService.getProductById(999L)).thenThrow(new ResourceNotFoundException("Producto no encontrado"));

    mockMvc.perform(get("/api/productos/999"))
        .andExpect(status().isNotFound());
    }

    @Test
    void getProductById_deberiaRetornar200ConProducto() throws Exception {
    Long id = 1L;
    ProductDTO producto = new ProductDTO(id, "Laptop", "Gaming", 1500.0, 10, 2L, "img.jpg");

    when(productService.getProductById(id)).thenReturn(producto);

    mockMvc.perform(get("/api/productos/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.nombre").value("Laptop"));
    }

    @Test
    void getProductById_deberiaRetornar404_siProductoNoExiste() throws Exception {
    Long id = 99L;

    when(productService.getProductById(id))
        .thenThrow(new ResourceNotFoundException("Producto no encontrado"));

    mockMvc.perform(get("/api/productos/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Producto no encontrado"));
    }

    @Test
    void searchProducts_deberiaRetornar200ConLista() throws Exception {
    String keyword = "laptop";
    List<ProductDTO> productos = List.of(
        new ProductDTO(1L, "Laptop Gamer", "Alta gama", 2000.0, 5, 1L, "url1.jpg")
    );

    when(productService.searchProducts(keyword)).thenReturn(productos);

    mockMvc.perform(get("/api/productos/buscar/{palabra_clave}", keyword))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].nombre").value("Laptop Gamer"));
    }

    @Test
    void searchProducts_deberiaRetornar200ConListaVacia_siNoCoincide() throws Exception {
    String keyword = "xyz";

    when(productService.searchProducts(keyword)).thenReturn(List.of());

    mockMvc.perform(get("/api/productos/buscar/{palabra_clave}", keyword))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllProducts_deberiaRetornar200ConLista() throws Exception {
    List<ProductDTO> productos = List.of(
        new ProductDTO(1L, "Producto 1", "Desc", 100.0, 10, 1L, "url.jpg")
    );

    when(productService.getAllProducts()).thenReturn(productos);

    mockMvc.perform(get("/api/productos/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].nombre").value("Producto 1"));
    }

    @Test
    void getProductById_deberiaRetornar500_siOcurreErrorInterno() throws Exception {
    Long productoId = 1L;

    when(productService.getProductById(productoId))
            .thenThrow(new RuntimeException("Fallo inesperado"));

    mockMvc.perform(get("/api/productos/{id}", productoId))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.error").value("Internal Server Error"))
            .andExpect(jsonPath("$.message").value("Ha ocurrido un error inesperado"));
    }


    @Test
    void createProduct_deberiaRetornarProductoCreado() throws Exception {
    ProductDTO productoEntrada = new ProductDTO(null, "Camisa", "Camisa de algodón", 59.99, 20, 1L, "img.jpg");
    ProductDTO productoRespuesta = new ProductDTO(1L, "Camisa", "Camisa de algodón", 59.99, 20, 1L, "img.jpg");

    when(productService.createProduct(any(ProductDTO.class))).thenReturn(productoRespuesta);

    mockMvc.perform(post("/api/productos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productoEntrada)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.nombre").value("Camisa"));
    }

    @Test
    void createProduct_deberiaRetornar404SiCategoriaNoExiste() throws Exception {
    ProductDTO productoEntrada = new ProductDTO(null, "Camisa", "Camisa de algodón", 59.99, 20, 999L, "img.jpg");

    when(productService.createProduct(any(ProductDTO.class)))
        .thenThrow(new ResourceNotFoundException("Categoría no encontrada"));

    mockMvc.perform(post("/api/productos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productoEntrada)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Categoría no encontrada"));
    }

    @Test
    void crearProducto_deberiaRetornarProductoCreado() throws Exception {
    ProductDTO producto = new ProductDTO(null, "Producto Test", "Descripción", 10.0, 5, 1L, "img.jpg");
    ProductDTO respuesta = new ProductDTO(1L, "Producto Test", "Descripción", 10.0, 5, 1L, "img.jpg");

    when(productService.createProduct(any(ProductDTO.class))).thenReturn(respuesta);

    mockMvc.perform(post("/api/productos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(producto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.nombre").value("Producto Test"));
    }


    @Test
    void crearProducto_deberiaRetornar404_siCategoriaNoExiste() throws Exception {
    ProductDTO producto = new ProductDTO(null, "Producto Test", "Descripción", 10.0, 5, 999L, "img.jpg");

    when(productService.createProduct(any(ProductDTO.class)))
        .thenThrow(new ResourceNotFoundException("Categoría no encontrada"));

    mockMvc.perform(post("/api/productos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(producto)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Categoría no encontrada"));
    }   

    @Test
    void validarStock_deberiaRetornar200_siTodoOk() throws Exception {
    List<OrderItem> items = List.of(new OrderItem(1L, 2));

    doNothing().when(productService).validarStock(items);

    mockMvc.perform(post("/api/productos/validar-stock")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(items)))
            .andExpect(status().isOk());
    }

    @Test
    void validarStock_deberiaRetornar400_siStockInsuficiente() throws Exception {
    List<OrderItem> items = List.of(new OrderItem(1L, 100));

    doThrow(new IllegalStateException("Stock insuficiente")).when(productService).validarStock(items);

    mockMvc.perform(post("/api/productos/validar-stock")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(items)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Stock insuficiente"));
    }

    @Test
    void crearProducto_deberiaRetornar400_siNombreEsNulo() throws Exception {
    ProductDTO producto = new ProductDTO(null, null, "Descripción", 10.0, 5, 1L, "img.jpg");

    mockMvc.perform(post("/api/productos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(producto)))
            .andExpect(status().isBadRequest());
    }

    

    @Test
    void actualizarProducto_deberiaRetornar200_siProductoEsValido() throws Exception {
    ProductDTO productoActualizado = new ProductDTO(1L, "Nuevo Nombre", "Nueva descripción", 100.0, 20, 1L, "imagen.png");

    when(productService.updateProduct(eq(1L), any(ProductDTO.class))).thenReturn(productoActualizado);

    mockMvc.perform(put("/api/productos/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productoActualizado)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nombre").value("Nuevo Nombre"))
        .andExpect(jsonPath("$.descripcion").value("Nueva descripción"))
        .andExpect(jsonPath("$.precio").value(100.0))
        .andExpect(jsonPath("$.stock").value(20))
        .andExpect(jsonPath("$.categoriaId").value(1L))
        .andExpect(jsonPath("$.imagenUrl").value("imagen.png"));

    verify(productService).updateProduct(eq(1L), any(ProductDTO.class));
    }

    @Test
    void actualizarProducto_deberiaRetornar404_siProductoNoExiste() throws Exception {
    ProductDTO producto = new ProductDTO(null, "Nombre", "Descripción", 100.0, 10, 1L, "imagen.png");

    when(productService.updateProduct(eq(99L), any(ProductDTO.class)))
        .thenThrow(new ResourceNotFoundException("Producto no encontrado"));

    mockMvc.perform(put("/api/productos/99")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(producto)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Producto no encontrado"))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void actualizarProducto_deberiaRetornar400_siNombreEsVacio() throws Exception {
    ProductDTO productoInvalido = new ProductDTO(1L, "", "Descripción", 100.0, 10, 1L, "imagen.png");

    mockMvc.perform(put("/api/productos/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productoInvalido)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void actualizarStock_deberiaRetornar200_siExitoso() throws Exception {
    Long productoId = 1L;
    Integer cantidad = 5;

    doNothing().when(productService).actualizarStock(productoId, cantidad);

    mockMvc.perform(put("/api/productos/{productoId}/stock", productoId)
            .param("cantidad", String.valueOf(cantidad)))
        .andExpect(status().isOk());
    }

    @Test
    void actualizarStock_deberiaRetornar404_siProductoNoExiste() throws Exception {
    Long productoId = 99L;
    Integer cantidad = 5;

    doThrow(new ResourceNotFoundException("Producto no encontrado"))
        .when(productService).actualizarStock(productoId, cantidad);

    mockMvc.perform(put("/api/productos/{productoId}/stock", productoId)
            .param("cantidad", String.valueOf(cantidad)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Producto no encontrado"));
    }

    @Test
    void actualizarStock_deberiaRetornar400_siCantidadInvalida() throws Exception {
    Long productoId = 1L;
    Integer cantidad = -10;

    doThrow(new IllegalArgumentException("Cantidad inválida"))
        .when(productService).actualizarStock(productoId, cantidad);

    mockMvc.perform(put("/api/productos/{productoId}/stock", productoId)
            .param("cantidad", String.valueOf(cantidad)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Cantidad inválida"));
    }

    @Test
    void actualizarStock_deberiaRetornar400_siCantidadEsNegativa() throws Exception {
    // Arrange
    Long productoId = 1L;
    int cantidad = -5;

    doThrow(new IllegalArgumentException("La cantidad no puede ser negativa"))
            .when(productService).actualizarStock(productoId, cantidad);

    // Act & Assert
    mockMvc.perform(put("/api/productos/{id}/stock", productoId)
            .param("cantidad", String.valueOf(cantidad)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("La cantidad no puede ser negativa"));
    }



    @Test
    void eliminarProducto_deberiaRetornar204_siProductoExiste() throws Exception {
    Long id = 1L;

    doNothing().when(productService).deleteProduct(id);

    mockMvc.perform(delete("/api/productos/{id}", id))
        .andExpect(status().isNoContent());
    }

    @Test
    void eliminarProducto_deberiaRetornar404_siProductoNoExiste() throws Exception {
    Long id = 100L;

    doThrow(new ResourceNotFoundException("Producto no encontrado")).when(productService).deleteProduct(id);

    mockMvc.perform(delete("/api/productos/{id}", id))
        .andExpect(status().isNotFound());
    }


}
