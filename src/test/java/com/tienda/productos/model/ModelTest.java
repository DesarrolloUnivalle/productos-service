package com.tienda.productos.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void testCategoriaModel() {
        Categoria categoria = new Categoria("Electrónica");
        categoria.setId(1L);
        categoria.setProductos(Collections.emptyList());

        assertEquals(1L, categoria.getId());
        assertEquals("Electrónica", categoria.getNombre());
        assertNotNull(categoria.getProductos());
    }

    @Test
    void testProductModel() {
        Categoria categoria = new Categoria("Ropa");
        categoria.setId(2L);

        Product product = new Product();
        product.setId(10L);
        product.setNombre("Zapatos");
        product.setDescripcion("Zapatos deportivos");
        product.setPrecio(150.0);
        product.setStock(20);
        product.setImagenUrl("http://imagen.com/zapatos.jpg");
        product.setCategoria(categoria);

        assertEquals(10L, product.getId());
        assertEquals("Zapatos", product.getNombre());
        assertEquals("Zapatos deportivos", product.getDescripcion());
        assertEquals(150.0, product.getPrecio());
        assertEquals(20, product.getStock());
        assertEquals("http://imagen.com/zapatos.jpg", product.getImagenUrl());
        assertEquals("Ropa", product.getCategoria().getNombre());
    }

    @Test
    void testOrderItemModel() {
        OrderItem item = new OrderItem(5L, 3);
        assertEquals(5L, item.getProductoId());
        assertEquals(3, item.getCantidad());

        item.setProductoId(8L);
        item.setCantidad(10);
        assertEquals(8L, item.getProductoId());
        assertEquals(10, item.getCantidad());
    }
}
