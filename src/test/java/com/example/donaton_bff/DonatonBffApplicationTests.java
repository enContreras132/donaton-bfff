package com.example.donaton_bff;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DonatonBffApplicationTests {

    @Test
    void deberiaCargarClasePrincipal() {
        DonatonBffApplication app = new DonatonBffApplication();
        assertNotNull(app);
    }
}
