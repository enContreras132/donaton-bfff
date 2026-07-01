package com.example.donaton_bff.controller;

import com.example.donaton_bff.service.BffService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NecesidadControllerTest {

    @Mock
    private BffService bffService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deberiaListarNecesidades() {
        NecesidadController controller = new NecesidadController(bffService);
        JsonNode node = objectMapper.createObjectNode().put("id", 1L);

        when(bffService.proxyGetForList("/necesidades", JsonNode.class))
                .thenReturn(List.of(node));

        ResponseEntity<List<JsonNode>> response = controller.listarNecesidades();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void deberiaObtenerNecesidadPorId() {
        NecesidadController controller = new NecesidadController(bffService);
        JsonNode node = objectMapper.createObjectNode().put("id", 1L);

        when(bffService.proxyGetForObject("/necesidades/1", JsonNode.class))
                .thenReturn(node);

        ResponseEntity<JsonNode> response = controller.obtenerNecesidad(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, response.getBody().get("id").asLong());
    }

    @Test
    void deberiaRetornarNotFoundCuandoNoExisteNecesidad() {
        NecesidadController controller = new NecesidadController(bffService);

        when(bffService.proxyGetForObject("/necesidades/99", JsonNode.class))
                .thenReturn(null);

        ResponseEntity<JsonNode> response = controller.obtenerNecesidad(99L);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void deberiaCrearActualizarEliminarYCambiarEstado() {
        NecesidadController controller = new NecesidadController(bffService);
        JsonNode body = objectMapper.createObjectNode().put("recursoNecesitado", "Alimentos");
        JsonNode responseNode = objectMapper.createObjectNode().put("id", 1L);

        when(bffService.proxyPost("/necesidades", body, JsonNode.class)).thenReturn(responseNode);
        when(bffService.proxyPut("/necesidades/1", body, JsonNode.class)).thenReturn(responseNode);
        when(bffService.proxyPatch("/necesidades/1/estado/CUBIERTA", JsonNode.class)).thenReturn(responseNode);

        assertEquals(200, controller.crearNecesidad(body).getStatusCode().value());
        assertEquals(200, controller.actualizarNecesidad(1L, body).getStatusCode().value());
        assertEquals(200, controller.cambiarEstado(1L, "CUBIERTA").getStatusCode().value());
        assertEquals(204, controller.eliminarNecesidad(1L).getStatusCode().value());

        verify(bffService).proxyDelete("/necesidades/1");
    }

    @Test
    void deberiaBuscarPorEstadoYUbicacion() {
        NecesidadController controller = new NecesidadController(bffService);
        JsonNode node = objectMapper.createObjectNode().put("id", 1L);

        when(bffService.proxyGetForList("/necesidades/estado/PENDIENTE", JsonNode.class))
                .thenReturn(List.of(node));
        when(bffService.proxyGetForList("/necesidades/ubicacion?valor=Santiago", JsonNode.class))
                .thenReturn(List.of(node));

        assertEquals(200, controller.porEstado("PENDIENTE").getStatusCode().value());
        assertEquals(200, controller.porUbicacion("Santiago").getStatusCode().value());
    }
}
