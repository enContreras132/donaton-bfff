package com.example.donaton_bff.controller;

import com.example.donaton_bff.dto.request.LoginRequest;
import com.example.donaton_bff.dto.response.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String usuario = request.getUsuario();
        String contrasena = request.getContrasena();

        if (usuario == null || usuario.isBlank() || contrasena == null || contrasena.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        boolean isAdmin = usuario.toLowerCase().contains("admin");
        String rol = isAdmin ? "admin" : "usuario";
        String token = UUID.randomUUID().toString();

        LoginResponse response = new LoginResponse(usuario, token, rol);
        return ResponseEntity.ok(response);
    }
}
