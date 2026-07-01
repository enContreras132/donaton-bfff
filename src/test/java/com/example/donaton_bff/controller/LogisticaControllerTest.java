package com.example.donaton_bff.controller;

import com.example.donaton_bff.dto.response.DistribucionDetalleResponse;
import com.example.donaton_bff.service.BffService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogisticaControllerTest {

    @Mock
    private BffService bffService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deberiaCubrirOperacionesDeEnvios() {
        LogisticaController controller = new LogisticaController(bffService);
        JsonNode body = objectMapper.createObjectNode().put("destino", "Santiago");
        JsonNode node = objectMapper.createObjectNode().put("id", 1L);

        when(bffService.proxyGetForList("/envios", JsonNode.class)).thenReturn(List.of(node));
        when(bffService.proxyGetForObject("/envios/1", JsonNode.class)).thenReturn(node);
        when(bffService.proxyPost("/envios", body, JsonNode.class)).thenReturn(node);
        when(bffService.proxyPatch("/envios/1/estado/ENTREGADO", JsonNode.class)).thenReturn(node);
        when(bffService.proxyGetForList("/envios/estado/PLANIFICADO", JsonNode.class)).thenReturn(List.of(node));
        when(bffService.proxyGetForList("/envios/donacion/10", JsonNode.class)).thenReturn(List.of(node));
        when(bffService.proxyGetForList("/envios/necesidad/20", JsonNode.class)).thenReturn(List.of(node));

        assertEquals(200, controller.listarEnvios().getStatusCode().value());
        assertEquals(200, controller.obtenerEnvio(1L).getStatusCode().value());
        assertEquals(200, controller.crearEnvio(body).getStatusCode().value());
        assertEquals(200, controller.cambiarEstadoEnvio(1L, "ENTREGADO").getStatusCode().value());
        assertEquals(200, controller.enviosPorEstado("PLANIFICADO").getStatusCode().value());
        assertEquals(200, controller.enviosPorDonacion(10L).getStatusCode().value());
        assertEquals(200, controller.enviosPorNecesidad(20L).getStatusCode().value());
        assertEquals(204, controller.eliminarEnvio(1L).getStatusCode().value());

        verify(bffService).proxyDelete("/envios/1");
    }

    @Test
    void deberiaRetornarNotFoundCuandoEnvioNoExiste() {
        LogisticaController controller = new LogisticaController(bffService);

        when(bffService.proxyGetForObject("/envios/99", JsonNode.class)).thenReturn(null);

        assertEquals(404, controller.obtenerEnvio(99L).getStatusCode().value());
    }

    @Test
    void deberiaCubrirOperacionesDeCentrosAcopio() {
        LogisticaController controller = new LogisticaController(bffService);
        JsonNode body = objectMapper.createObjectNode().put("nombre", "Centro Norte");
        JsonNode node = objectMapper.createObjectNode().put("id", 1L);

        when(bffService.proxyGetForList("/centros-acopio", JsonNode.class)).thenReturn(List.of(node));
        when(bffService.proxyGetForList("/centros-acopio/activos", JsonNode.class)).thenReturn(List.of(node));
        when(bffService.proxyGetForObject("/centros-acopio/1", JsonNode.class)).thenReturn(node);
        when(bffService.proxyPost("/centros-acopio", body, JsonNode.class)).thenReturn(node);
        when(bffService.proxyPut("/centros-acopio/1", body, JsonNode.class)).thenReturn(node);
        when(bffService.proxyPatch("/centros-acopio/1/activo/true", JsonNode.class)).thenReturn(node);

        assertEquals(200, controller.listarCentrosAcopio().getStatusCode().value());
        assertEquals(200, controller.centrosActivos().getStatusCode().value());
        assertEquals(200, controller.obtenerCentroAcopio(1L).getStatusCode().value());
        assertEquals(200, controller.crearCentroAcopio(body).getStatusCode().value());
        assertEquals(200, controller.actualizarCentroAcopio(1L, body).getStatusCode().value());
        assertEquals(200, controller.cambiarEstadoCentro(1L, true).getStatusCode().value());
        assertEquals(204, controller.eliminarCentroAcopio(1L).getStatusCode().value());

        verify(bffService).proxyDelete("/centros-acopio/1");
    }

    @Test
    void deberiaRetornarNotFoundCuandoCentroNoExiste() {
        LogisticaController controller = new LogisticaController(bffService);

        when(bffService.proxyGetForObject("/centros-acopio/99", JsonNode.class)).thenReturn(null);

        assertEquals(404, controller.obtenerCentroAcopio(99L).getStatusCode().value());
    }

    @Test
    void deberiaObtenerDetalleDistribucion() {
        LogisticaController controller = new LogisticaController(bffService);
        DistribucionDetalleResponse detalle = DistribucionDetalleResponse.builder().id(1L).build();

        when(bffService.getDistribucionDetalle(1L)).thenReturn(detalle);

        assertEquals(200, controller.detalleDistribucion(1L).getStatusCode().value());
    }

    @Test
    void deberiaRetornarNotFoundCuandoDetalleDistribucionNoExiste() {
        LogisticaController controller = new LogisticaController(bffService);

        when(bffService.getDistribucionDetalle(99L)).thenReturn(null);

        assertEquals(404, controller.detalleDistribucion(99L).getStatusCode().value());
    }
}
