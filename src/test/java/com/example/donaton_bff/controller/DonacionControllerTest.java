package com.example.donaton_bff.controller;

import com.example.donaton_bff.dto.response.DonacionDetalleResponse;
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
class DonacionControllerTest {

    @Mock
    private BffService bffService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deberiaListarYObtenerDonaciones() {
        DonacionController controller = new DonacionController(bffService, objectMapper);
        JsonNode node = objectMapper.createObjectNode().put("id", 1L);

        when(bffService.proxyGetForList("/donaciones", JsonNode.class)).thenReturn(List.of(node));
        when(bffService.proxyGetForObject("/donaciones/1", JsonNode.class)).thenReturn(node);

        assertEquals(200, controller.listarDonaciones().getStatusCode().value());
        assertEquals(200, controller.obtenerDonacion(1L).getStatusCode().value());
    }

    @Test
    void deberiaRetornarNotFoundCuandoNoExisteDonacion() {
        DonacionController controller = new DonacionController(bffService, objectMapper);

        when(bffService.proxyGetForObject("/donaciones/99", JsonNode.class)).thenReturn(null);

        ResponseEntity<JsonNode> response = controller.obtenerDonacion(99L);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void deberiaCrearActualizarEliminarYCambiarEstadoDonacion() {
        DonacionController controller = new DonacionController(bffService, objectMapper);
        JsonNode body = objectMapper.createObjectNode().put("tipoRecurso", "ALIMENTO");
        JsonNode normalizada = objectMapper.createObjectNode().put("tipoRecurso", "ALIMENTO");
        JsonNode responseNode = objectMapper.createObjectNode().put("id", 1L);

        when(bffService.normalizarCrearDonacion(body)).thenReturn(normalizada);
        when(bffService.proxyPost("/donaciones", normalizada, JsonNode.class)).thenReturn(responseNode);
        when(bffService.proxyPut("/donaciones/1", body, JsonNode.class)).thenReturn(responseNode);
        when(bffService.proxyPatch("/donaciones/1/estado/ENTREGADA", JsonNode.class)).thenReturn(responseNode);

        assertEquals(200, controller.crearDonacion(body).getStatusCode().value());
        assertEquals(200, controller.actualizarDonacion(1L, body).getStatusCode().value());
        assertEquals(200, controller.cambiarEstado(1L, "ENTREGADA").getStatusCode().value());
        assertEquals(204, controller.eliminarDonacion(1L).getStatusCode().value());

        verify(bffService).proxyDelete("/donaciones/1");
    }

    @Test
    void deberiaBuscarPorEstadoTipoYDetalle() {
        DonacionController controller = new DonacionController(bffService, objectMapper);
        JsonNode node = objectMapper.createObjectNode().put("id", 1L);
        DonacionDetalleResponse detalle = DonacionDetalleResponse.builder().id(1L).build();

        when(bffService.proxyGetForList("/donaciones/estado/REGISTRADA", JsonNode.class)).thenReturn(List.of(node));
        when(bffService.proxyGetForList("/donaciones/tipo/ALIMENTO", JsonNode.class)).thenReturn(List.of(node));
        when(bffService.getDonacionDetalle(1L)).thenReturn(detalle);

        assertEquals(200, controller.porEstado("REGISTRADA").getStatusCode().value());
        assertEquals(200, controller.porTipo("ALIMENTO").getStatusCode().value());
        assertEquals(200, controller.detalleDonacion(1L).getStatusCode().value());
    }

    @Test
    void deberiaRetornarNotFoundCuandoDetalleNoExiste() {
        DonacionController controller = new DonacionController(bffService, objectMapper);

        when(bffService.getDonacionDetalle(99L)).thenReturn(null);

        assertEquals(404, controller.detalleDonacion(99L).getStatusCode().value());
    }
}
