package com.tienda.productos.service;

import com.tienda.productos.ProductosApplication;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductosApplicationOpenApiTest {

    @Test
    void customOpenAPIDebeRetornarBeanConInfoCorrecta() {
        ProductosApplication app = new ProductosApplication();
        OpenAPI openAPI = app.customOpenAPI();

        assertThat(openAPI).isNotNull();
        Info info = openAPI.getInfo();
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("Productos API");
        assertThat(info.getVersion()).isEqualTo("1.0");
        assertThat(info.getDescription()).isEqualTo("API para la gestión de productos");
    }
}
