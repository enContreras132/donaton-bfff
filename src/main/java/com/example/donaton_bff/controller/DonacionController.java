package com.example.donaton_bff.controller;

import com.example.donaton_bff.dto.response.DonacionDetalleResponse;
import com.example.donaton_bff.service.BffService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/donaciones")
public class DonacionController {

    private final BffService bffService;
    private final ObjectMapper objectMapper;

    public DonacionController(BffService bffService, ObjectMapper objectMapper) {
        this.bffService = bffService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<List<JsonNode>> listarDonaciones() {
        List<JsonNode> donaciones = bffService.proxyGetForList("/donaciones", JsonNode.class);
        return ResponseEntity.ok(donaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JsonNode> obtenerDonacion(@PathVariable("id") Long id) {
        JsonNode donacion = bffService.proxyGetForObject("/donaciones/" + id, JsonNode.class);
        if (donacion == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(donacion);
    }

    @PostMapping
    public ResponseEntity<JsonNode> crearDonacion(@RequestBody JsonNode body) {
        JsonNode normalizada = bffService.normalizarCrearDonacion(body);
        JsonNode creada = bffService.proxyPost("/donaciones", normalizada, JsonNode.class);
        return ResponseEntity.ok(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JsonNode> actualizarDonacion(@PathVariable("id") Long id, @RequestBody JsonNode body) {
        JsonNode actualizada = bffService.proxyPut("/donaciones/" + id, body, JsonNode.class);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDonacion(@PathVariable("id") Long id) {
        bffService.proxyDelete("/donaciones/" + id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado/{estado}")
    public ResponseEntity<JsonNode> cambiarEstado(@PathVariable("id") Long id, @PathVariable("estado") String estado) {
        JsonNode result = bffService.proxyPatch("/donaciones/" + id + "/estado/" + estado, JsonNode.class);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<JsonNode>> porEstado(@PathVariable("estado") String estado) {
        List<JsonNode> result = bffService.proxyGetForList("/donaciones/estado/" + estado, JsonNode.class);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<JsonNode>> porTipo(@PathVariable("tipo") String tipo) {
        List<JsonNode> result = bffService.proxyGetForList("/donaciones/tipo/" + tipo, JsonNode.class);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/detalle")
    public ResponseEntity<DonacionDetalleResponse> detalleDonacion(@PathVariable("id") Long id) {
        DonacionDetalleResponse detalle = bffService.getDonacionDetalle(id);
        if (detalle == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(detalle);
    }
}
