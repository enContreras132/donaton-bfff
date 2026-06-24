package com.example.donaton_bff.controller;

import com.example.donaton_bff.service.BffService;
import tools.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/necesidades")
public class NecesidadController {

    private final BffService bffService;

    public NecesidadController(BffService bffService) {
        this.bffService = bffService;
    }

    @GetMapping
    public ResponseEntity<List<JsonNode>> listarNecesidades() {
        List<JsonNode> necesidades = bffService.proxyGetForList("/necesidades", JsonNode.class);
        return ResponseEntity.ok(necesidades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JsonNode> obtenerNecesidad(@PathVariable Long id) {
        JsonNode necesidad = bffService.proxyGetForObject("/necesidades/" + id, JsonNode.class);
        if (necesidad == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(necesidad);
    }

    @PostMapping
    public ResponseEntity<JsonNode> crearNecesidad(@RequestBody JsonNode body) {
        JsonNode creada = bffService.proxyPost("/necesidades", body, JsonNode.class);
        return ResponseEntity.ok(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JsonNode> actualizarNecesidad(@PathVariable Long id, @RequestBody JsonNode body) {
        JsonNode actualizada = bffService.proxyPut("/necesidades/" + id, body, JsonNode.class);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNecesidad(@PathVariable Long id) {
        bffService.proxyDelete("/necesidades/" + id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado/{estado}")
    public ResponseEntity<JsonNode> cambiarEstado(@PathVariable Long id, @PathVariable String estado) {
        JsonNode result = bffService.proxyPatch("/necesidades/" + id + "/estado/" + estado, JsonNode.class);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<JsonNode>> porEstado(@PathVariable String estado) {
        List<JsonNode> result = bffService.proxyGetForList("/necesidades/estado/" + estado, JsonNode.class);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/ubicacion")
    public ResponseEntity<List<JsonNode>> porUbicacion(@RequestParam String valor) {
        List<JsonNode> result = bffService.proxyGetForList("/necesidades/ubicacion?valor=" + valor, JsonNode.class);
        return ResponseEntity.ok(result);
    }
}
