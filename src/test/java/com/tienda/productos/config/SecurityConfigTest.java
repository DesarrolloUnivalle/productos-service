package com.tienda.productos.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    private final SecurityConfig config = new SecurityConfig();

    @Test
    void contextLoads_securityConfigShouldBePresent() {
        assertThat(securityFilterChain).isNotNull();
    }

    @Test
    void securityFilterChainShouldNotBeNull() throws Exception {
        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);

        SecurityFilterChain chain = config.securityFilterChain(http);

        assertThat(chain).isNotNull();
    }

    @Test
    void corsConfigurationSourceShouldBeCorrectlyConfigured() {
        UrlBasedCorsConfigurationSource source = (UrlBasedCorsConfigurationSource) config.corsConfigurationSource();
        CorsConfiguration configuration = source.getCorsConfigurations().get("/**");

        assertThat(configuration).isNotNull();
        assertThat(configuration.getAllowedOrigins())
                .containsExactlyInAnyOrder("http://localhost:8080", "http://localhost:8081", "http://localhost:8082");

        assertThat(configuration.getAllowedMethods())
                .containsExactlyInAnyOrder("GET", "POST", "PUT", "DELETE", "OPTIONS");

        assertThat(configuration.getAllowedHeaders())
                .contains("Authorization", "Content-Type", "X-Requested-With");

        assertThat(configuration.getExposedHeaders())
                .contains("Authorization");

        assertThat(configuration.getAllowCredentials()).isTrue();
    }

}

