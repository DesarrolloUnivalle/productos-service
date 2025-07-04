package com.tienda.productos.repository;

import com.tienda.productos.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNombreContainingIgnoreCase(String nombre);
}
