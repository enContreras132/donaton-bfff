package com.example.donaton_bff.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import static org.junit.jupiter.api.Assertions.*;

class BffServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private BffService crearService() {
        return new BffService(RestClient.builder().build(), objectMapper,
                "http://donaciones-service/api/v1/donaciones",
                "http://necesidades-service/api/v1/necesidades",
                "http://logistica-service/api/v1");
    }

    @Test
    void deberiaNormalizarDonacionConDatosDirectos() {
        BffService service = crearService();

        ObjectNode body = objectMapper.createObjectNode();
        body.put("tipoRecurso", "alimento");
        body.put("detalleRecurso", "Arroz");
        body.put("cantidad", 5);
        body.put("origen", "Santiago");
        body.put("nombreDonante", "Juan");
        body.put("contactoDonante", "juan@test.com");
        body.put("centroAcopioId", 1L);
        body.put("necesidadId", 2L);

        JsonNode resultado = service.normalizarCrearDonacion(body);

        assertEquals("ALIMENTO", resultado.get("tipoRecurso").asText());
        assertEquals("Arroz", resultado.get("detalleRecurso").asText());
        assertEquals(5, resultado.get("cantidad").asInt());
        assertEquals("Santiago", resultado.get("origen").asText());
        assertEquals("Juan", resultado.get("nombreDonante").asText());
        assertEquals("juan@test.com", resultado.get("contactoDonante").asText());
        assertEquals(1L, resultado.get("centroAcopioId").asLong());
        assertEquals(2L, resultado.get("necesidadId").asLong());
    }

    @Test
    void deberiaNormalizarDonacionConDetallesAlternativos() {
        BffService service = crearService();

        ObjectNode detalles = objectMapper.createObjectNode();
        detalles.put("donador", "María");
        detalles.put("email", "maria@test.com");
        detalles.put("cantidad", 3);
        detalles.put("producto", "Botellas de agua");

        ObjectNode body = objectMapper.createObjectNode();
        body.set("detalles", detalles);
        body.put("unidad", "litros");

        JsonNode resultado = service.normalizarCrearDonacion(body);

        assertEquals("OTRO", resultado.get("tipoRecurso").asText());
        assertTrue(resultado.get("detalleRecurso").asText().contains("Botellas de agua"));
        assertEquals(3, resultado.get("cantidad").asInt());
        assertEquals("María", resultado.get("origen").asText());
        assertEquals("María", resultado.get("nombreDonante").asText());
        assertEquals("maria@test.com", resultado.get("contactoDonante").asText());
        assertTrue(resultado.get("centroAcopioId").isNull());
        assertTrue(resultado.get("necesidadId").isNull());
    }
}
