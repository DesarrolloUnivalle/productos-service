package com.tienda.productos.service.impl;

import com.tienda.productos.dto.ProductDTO;
import com.tienda.productos.exception.ResourceNotFoundException;
import com.tienda.productos.model.Categoria;
import com.tienda.productos.model.Product;
import com.tienda.productos.model.OrderItem;
import com.tienda.productos.repository.CategoriaRepository;
import com.tienda.productos.repository.ProductRepository;
import java.util.List;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProduct_shouldReturnSavedProduct() {
        ProductDTO dto = new ProductDTO();
        dto.setNombre("Camiseta");
        dto.setDescripcion("Camiseta blanca");
        dto.setPrecio(19.99);
        dto.setStock(50);
        dto.setImagenUrl("http://imagen.jpg");
        dto.setCategoriaId(1L);

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Ropa");

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setNombre(dto.getNombre());
        savedProduct.setDescripcion(dto.getDescripcion());
        savedProduct.setPrecio(dto.getPrecio());
        savedProduct.setStock(dto.getStock());
        savedProduct.setImagenUrl(dto.getImagenUrl());
        savedProduct.setCategoria(categoria);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductDTO result = productService.createProduct(dto);

        assertNotNull(result);
        assertEquals("Camiseta", result.getNombre());
        assertEquals(1L, result.getCategoriaId());
    }

    @Test
    void createProduct_shouldThrowWhenCategoriaNotFound() {
        ProductDTO dto = new ProductDTO();
        dto.setCategoriaId(99L);

        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(dto));
    }

    @Test
    void getProductById_shouldReturnProductDTO() {
        Product product = new Product();
        product.setId(10L);
        product.setNombre("Zapatos");
        product.setDescripcion("Zapatos de cuero");
        product.setPrecio(49.99);
        product.setStock(30);

        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        ProductDTO result = productService.getProductById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Zapatos", result.getNombre());
    }

    @Test
    void getProductById_shouldThrowWhenNotFound() {
        when(productRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(123L));
    }

       @Test
    void updateProduct_deberiaActualizarYRetornarDTO() {
        // Arrange
        Long id = 1L;
        Long categoriaId = 10L;

        Product productoExistente = new Product();
        productoExistente.setId(id);
        productoExistente.setNombre("Viejo nombre");
        productoExistente.setStock(5);

        Categoria categoria = new Categoria();
        categoria.setId(categoriaId);

        ProductDTO actualizadoDTO = new ProductDTO();
        actualizadoDTO.setNombre("Nuevo nombre");
        actualizadoDTO.setDescripcion("Desc nueva");
        actualizadoDTO.setPrecio(50.0);
        actualizadoDTO.setStock(10);
        actualizadoDTO.setImagenUrl("url/nueva.png");
        actualizadoDTO.setCategoriaId(categoriaId);

        Product productoActualizado = new Product();
        productoActualizado.setId(id);
        productoActualizado.setNombre("Nuevo nombre");
        productoActualizado.setDescripcion("Desc nueva");
        productoActualizado.setPrecio(50.0);
        productoActualizado.setStock(10);
        productoActualizado.setImagenUrl("url/nueva.png");
        productoActualizado.setCategoria(categoria);

        when(productRepository.findById(id)).thenReturn(Optional.of(productoExistente));
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(productRepository.save(any(Product.class))).thenReturn(productoActualizado);

        // Act
        ProductDTO result = productService.updateProduct(id, actualizadoDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Nuevo nombre", result.getNombre());
        assertEquals("Desc nueva", result.getDescripcion());
        assertEquals(50.0, result.getPrecio());
        assertEquals(10, result.getStock());
        assertEquals("url/nueva.png", result.getImagenUrl());
        assertEquals(categoriaId, result.getCategoriaId());

        verify(productRepository).findById(id);
        verify(categoriaRepository).findById(categoriaId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_deberiaLanzarExcepcionCuandoProductoNoExiste() {
    // Arrange
    Long id = 99L;
    ProductDTO dto = new ProductDTO();
    dto.setNombre("Nuevo nombre");

    when(productRepository.findById(id)).thenReturn(Optional.empty());

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
            productService.updateProduct(id, dto)
    );

    assertEquals("Producto no encontrado con id: " + id, exception.getMessage());
    verify(productRepository).findById(id);
    verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_deberiaLanzarExcepcionCuandoCategoriaNoExiste() {
    // Arrange
    Long productId = 1L;
    Long categoriaIdInvalida = 999L;

    ProductDTO dto = new ProductDTO();
    dto.setNombre("Nuevo nombre");
    dto.setDescripcion("Nueva descripción");
    dto.setPrecio(199.99);
    dto.setStock(50);
    dto.setImagenUrl("nueva.jpg");
    dto.setCategoriaId(categoriaIdInvalida);

    Product productoExistente = new Product();
    productoExistente.setId(productId);
    productoExistente.setNombre("Viejo nombre");

    when(productRepository.findById(productId)).thenReturn(Optional.of(productoExistente));
    when(categoriaRepository.findById(categoriaIdInvalida)).thenReturn(Optional.empty());

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
            productService.updateProduct(productId, dto)
    );

    assertEquals("Categoría no encontrada con id: " + categoriaIdInvalida, exception.getMessage());
    verify(productRepository).findById(productId);
    verify(categoriaRepository).findById(categoriaIdInvalida);
    verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_deberiaLanzarExcepcionCuandoProductoNoExiste() {
    // Arrange
    Long productId = 999L;
    when(productRepository.existsById(productId)).thenReturn(false);

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
            productService.deleteProduct(productId)
    );

    assertEquals("Producto no encontrado con id: " + productId, exception.getMessage());
    verify(productRepository).existsById(productId);
    verify(productRepository, never()).deleteById(any());
    }

    @Test
    void actualizarStock_deberiaActualizarStockCuandoHaySuficiente() {
    // Arrange
    Long productId = 1L;
    int stockInicial = 10;
    int cantidadReducir = 3;

    Product producto = new Product();
    producto.setId(productId);
    producto.setStock(stockInicial);

    when(productRepository.findById(productId)).thenReturn(Optional.of(producto));

    // Act
    productService.actualizarStock(productId, cantidadReducir);

    // Assert
    assertEquals(stockInicial - cantidadReducir, producto.getStock());
    verify(productRepository).save(producto);
    }

    @Test
    void actualizarStock_deberiaLanzarExcepcionSiProductoNoExiste() {
    // Arrange
    Long productId = 999L;
    when(productRepository.findById(productId)).thenReturn(Optional.empty());

    // Act & Assert
    ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
            productService.actualizarStock(productId, 5)
    );

    assertEquals("Producto no encontrado con id: " + productId, ex.getMessage());
    }

    @Test
    void actualizarStock_deberiaLanzarExcepcionSiStockInsuficiente() {
    // Arrange
    Long productId = 1L;
    int stockActual = 2;
    int cantidadSolicitada = 5;

    Product producto = new Product();
    producto.setId(productId);
    producto.setStock(stockActual);

    when(productRepository.findById(productId)).thenReturn(Optional.of(producto));

    // Act & Assert
    IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
            productService.actualizarStock(productId, cantidadSolicitada)
    );

    assertEquals("Stock insuficiente para el producto: " + productId, ex.getMessage());
    verify(productRepository, never()).save(any());
    }

    @Test
    void validarStock_deberiaPasarCuandoTodosTienenStockSuficiente() {
    // Arrange
    OrderItem item1 = new OrderItem();
    item1.setProductoId(1L);
    item1.setCantidad(2);

    OrderItem item2 = new OrderItem();
    item2.setProductoId(2L);
    item2.setCantidad(1);

    Product producto1 = new Product();
    producto1.setId(1L);
    producto1.setStock(5);

    Product producto2 = new Product();
    producto2.setId(2L);
    producto2.setStock(3);

    when(productRepository.findById(1L)).thenReturn(Optional.of(producto1));
    when(productRepository.findById(2L)).thenReturn(Optional.of(producto2));

    // Act & Assert
    assertDoesNotThrow(() -> productService.validarStock(List.of(item1, item2)));
    }

    @Test
    void validarStock_deberiaLanzarExcepcionSiProductoNoExiste() {
    // Arrange
    OrderItem item = new OrderItem();
    item.setProductoId(99L);
    item.setCantidad(1);

    when(productRepository.findById(99L)).thenReturn(Optional.empty());

    // Act & Assert
    ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
            productService.validarStock(List.of(item))
    );

    assertEquals("Producto no encontrado con id: 99", ex.getMessage());
    }

    @Test
    void validarStock_deberiaLanzarExcepcionSiStockInsuficiente() {
    // Arrange
    OrderItem item = new OrderItem();
    item.setProductoId(1L);
    item.setCantidad(10);

    Product producto = new Product();
    producto.setId(1L);
    producto.setStock(5);

    when(productRepository.findById(1L)).thenReturn(Optional.of(producto));

    // Act & Assert
    IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
            productService.validarStock(List.of(item))
    );

    assertEquals("Stock insuficiente para el producto: 1", ex.getMessage());
    }

    @Test
    void getAllProducts_deberiaRetornarListaDeProductosDTO() {
    // Arrange
    Product producto1 = new Product();
    producto1.setId(1L);
    producto1.setNombre("Producto 1");
    producto1.setDescripcion("Desc 1");
    producto1.setPrecio(10.0);
    producto1.setStock(100);
    producto1.setImagenUrl("url1");

    Product producto2 = new Product();
    producto2.setId(2L);
    producto2.setNombre("Producto 2");
    producto2.setDescripcion("Desc 2");
    producto2.setPrecio(20.0);
    producto2.setStock(50);
    producto2.setImagenUrl("url2");

    List<Product> listaProductos = List.of(producto1, producto2);

    when(productRepository.findAll()).thenReturn(listaProductos);

    // Act
    List<ProductDTO> resultado = productService.getAllProducts();

    // Assert
    assertNotNull(resultado);
    assertEquals(2, resultado.size());

    assertEquals(producto1.getId(), resultado.get(0).getId());
    assertEquals(producto1.getNombre(), resultado.get(0).getNombre());

    assertEquals(producto2.getId(), resultado.get(1).getId());
    assertEquals(producto2.getNombre(), resultado.get(1).getNombre());

    verify(productRepository, times(1)).findAll();
    }

    @Test
    void getAllProducts_deberiaRetornarListaVacia_cuandoNoHayProductos() {
    // Arrange
    when(productRepository.findAll()).thenReturn(Collections.emptyList());

    // Act
    List<ProductDTO> result = productService.getAllProducts();

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(productRepository, times(1)).findAll();
    }

    @Test
    void searchProducts_deberiaRetornarTodosLosProductos_cuandoPalabraClaveEsNula() {
    // Arrange
    Product product = new Product();
    product.setId(1L);
    product.setNombre("Camiseta");
    product.setDescripcion("Camiseta blanca");
    product.setPrecio(20000.0);
    product.setStock(10);
    product.setImagenUrl("imagen.jpg");

    when(productRepository.findAll()).thenReturn(List.of(product));

    // Act
    List<ProductDTO> result = productService.searchProducts(null);

    // Assert
    assertEquals(1, result.size());
    assertEquals("Camiseta", result.get(0).getNombre());
    verify(productRepository).findAll();
    }

    @Test
    void searchProducts_deberiaRetornarTodosLosProductos_cuandoPalabraClaveEsVacia() {
    // Arrange
    Product product = new Product();
    product.setId(1L);
    product.setNombre("Pantalón");
    product.setDescripcion("Pantalón negro");
    product.setPrecio(45000.0);
    product.setStock(5);
    product.setImagenUrl("img.jpg");

    when(productRepository.findAll()).thenReturn(List.of(product));

    // Act
    List<ProductDTO> result = productService.searchProducts("   ");

    // Assert
    assertEquals(1, result.size());
    assertEquals("Pantalón", result.get(0).getNombre());
    verify(productRepository).findAll();
    }

    @Test
    void searchProducts_deberiaRetornarProductosFiltradosPorPalabraClave() {
    // Arrange
    Product producto1 = new Product();
    producto1.setId(1L);
    producto1.setNombre("Zapatos deportivos");
    producto1.setDescripcion("Zapatos cómodos");
    producto1.setPrecio(80000.0);
    producto1.setStock(15);
    producto1.setImagenUrl("zap1.jpg");

    Product producto2 = new Product();
    producto2.setId(2L);
    producto2.setNombre("Zapatillas");
    producto2.setDescripcion("Para correr");
    producto2.setPrecio(70000.0);
    producto2.setStock(8);
    producto2.setImagenUrl("zap2.jpg");

    when(productRepository.findByNombreContainingIgnoreCase("zap"))
        .thenReturn(List.of(producto1, producto2));

    // Act
    List<ProductDTO> result = productService.searchProducts("zap");

    // Assert
    assertEquals(2, result.size());
    assertTrue(result.get(0).getNombre().toLowerCase().contains("zap"));
    assertTrue(result.get(1).getNombre().toLowerCase().contains("zap"));
    verify(productRepository).findByNombreContainingIgnoreCase("zap");
    }

    @Test
    void searchProducts_conPalabraClaveSinCoincidencias_deberiaRetornarListaVacia() {
    // Arrange
    when(productRepository.findByNombreContainingIgnoreCase("xyz"))
        .thenReturn(List.of());

    // Act
    List<ProductDTO> result = productService.searchProducts("xyz");

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(productRepository).findByNombreContainingIgnoreCase("xyz");
    }

    @Test
    void createProduct_deberiaCrearProductoSinCategoria_siCategoriaIdEsNull() {
    ProductDTO dto = new ProductDTO();
    dto.setNombre("Producto sin categoría");
    dto.setDescripcion("Sin categoría");
    dto.setPrecio(10.0);
    dto.setStock(5);
    dto.setImagenUrl("img.jpg");
    dto.setCategoriaId(null); // explícito

    Product product = new Product();
    product.setId(1L);
    product.setNombre(dto.getNombre());
    product.setDescripcion(dto.getDescripcion());
    product.setPrecio(dto.getPrecio());
    product.setStock(dto.getStock());
    product.setImagenUrl(dto.getImagenUrl());

    when(productRepository.save(any())).thenReturn(product);

    ProductDTO result = productService.createProduct(dto);

    assertNotNull(result);
    assertEquals(dto.getNombre(), result.getNombre());
    }

    @Test
    void updateProduct_deberiaActualizarSinCambiarCategoria_siCategoriaIdEsNull() {
    Long id = 1L;

    Product existente = new Product();
    existente.setId(id);
    existente.setNombre("Viejo");
    existente.setCategoria(new Categoria()); // ya tiene una categoría

    ProductDTO dto = new ProductDTO();
    dto.setNombre("Nuevo");
    dto.setDescripcion("Desc");
    dto.setPrecio(100.0);
    dto.setStock(5);
    dto.setImagenUrl("nuevo.jpg");
    dto.setCategoriaId(null); // explícitamente null

    when(productRepository.findById(id)).thenReturn(Optional.of(existente));
    when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    ProductDTO result = productService.updateProduct(id, dto);

    assertEquals("Nuevo", result.getNombre());
    verify(productRepository).save(any());
    }


}