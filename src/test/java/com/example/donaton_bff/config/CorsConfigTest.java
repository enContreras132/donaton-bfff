package com.example.donaton_bff.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.filter.CorsFilter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CorsConfigTest {

    @Test
    void deberiaCrearCorsFilter() {
        CorsConfig config = new CorsConfig();

        CorsFilter filter = config.corsFilter();

        assertNotNull(filter);
    }
}
