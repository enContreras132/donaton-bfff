package com.example.donaton_bff.controller;

import com.example.donaton_bff.dto.response.DistribucionDetalleResponse;
import com.example.donaton_bff.service.BffService;
import tools.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class LogisticaController {

    private final BffService bffService;

    public LogisticaController(BffService bffService) {
        this.bffService = bffService;
    }

    @GetMapping("/envios")
    public ResponseEntity<List<JsonNode>> listarEnvios() {
        List<JsonNode> envios = bffService.proxyGetForList("/envios", JsonNode.class);
        return ResponseEntity.ok(envios);
    }

    @GetMapping("/envios/{id}")
    public ResponseEntity<JsonNode> obtenerEnvio(@PathVariable Long id) {
        JsonNode envio = bffService.proxyGetForObject("/envios/" + id, JsonNode.class);
        if (envio == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(envio);
    }

    @PostMapping("/envios")
    public ResponseEntity<JsonNode> crearEnvio(@RequestBody JsonNode body) {
        JsonNode creado = bffService.proxyPost("/envios", body, JsonNode.class);
        return ResponseEntity.ok(creado);
    }

    @DeleteMapping("/envios/{id}")
    public ResponseEntity<Void> eliminarEnvio(@PathVariable Long id) {
        bffService.proxyDelete("/envios/" + id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/envios/{id}/estado/{estado}")
    public ResponseEntity<JsonNode> cambiarEstadoEnvio(@PathVariable Long id, @PathVariable String estado) {
        JsonNode result = bffService.proxyPatch("/envios/" + id + "/estado/" + estado, JsonNode.class);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/envios/estado/{estado}")
    public ResponseEntity<List<JsonNode>> enviosPorEstado(@PathVariable String estado) {
        List<JsonNode> result = bffService.proxyGetForList("/envios/estado/" + estado, JsonNode.class);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/envios/donacion/{donacionId}")
    public ResponseEntity<List<JsonNode>> enviosPorDonacion(@PathVariable Long donacionId) {
        List<JsonNode> result = bffService.proxyGetForList("/envios/donacion/" + donacionId, JsonNode.class);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/envios/necesidad/{necesidadId}")
    public ResponseEntity<List<JsonNode>> enviosPorNecesidad(@PathVariable Long necesidadId) {
        List<JsonNode> result = bffService.proxyGetForList("/envios/necesidad/" + necesidadId, JsonNode.class);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/centros-acopio")
    public ResponseEntity<List<JsonNode>> listarCentrosAcopio() {
        List<JsonNode> centros = bffService.proxyGetForList("/centros-acopio", JsonNode.class);
        return ResponseEntity.ok(centros);
    }

    @GetMapping("/centros-acopio/activos")
    public ResponseEntity<List<JsonNode>> centrosActivos() {
        List<JsonNode> centros = bffService.proxyGetForList("/centros-acopio/activos", JsonNode.class);
        return ResponseEntity.ok(centros);
    }

    @GetMapping("/centros-acopio/{id}")
    public ResponseEntity<JsonNode> obtenerCentroAcopio(@PathVariable Long id) {
        JsonNode centro = bffService.proxyGetForObject("/centros-acopio/" + id, JsonNode.class);
        if (centro == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(centro);
    }

    @PostMapping("/centros-acopio")
    public ResponseEntity<JsonNode> crearCentroAcopio(@RequestBody JsonNode body) {
        JsonNode creado = bffService.proxyPost("/centros-acopio", body, JsonNode.class);
        return ResponseEntity.ok(creado);
    }

    @PutMapping("/centros-acopio/{id}")
    public ResponseEntity<JsonNode> actualizarCentroAcopio(@PathVariable Long id, @RequestBody JsonNode body) {
        JsonNode actualizado = bffService.proxyPut("/centros-acopio/" + id, body, JsonNode.class);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/centros-acopio/{id}")
    public ResponseEntity<Void> eliminarCentroAcopio(@PathVariable Long id) {
        bffService.proxyDelete("/centros-acopio/" + id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/centros-acopio/{id}/activo/{activo}")
    public ResponseEntity<JsonNode> cambiarEstadoCentro(@PathVariable Long id, @PathVariable Boolean activo) {
        JsonNode result = bffService.proxyPatch("/centros-acopio/" + id + "/activo/" + activo, JsonNode.class);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/distribuciones/{id}/detalle")
    public ResponseEntity<DistribucionDetalleResponse> detalleDistribucion(@PathVariable Long id) {
        DistribucionDetalleResponse detalle = bffService.getDistribucionDetalle(id);
        if (detalle == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(detalle);
    }
}
