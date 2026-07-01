package com.example.donaton_bff.controller;

import com.example.donaton_bff.dto.request.LoginRequest;
import com.example.donaton_bff.dto.response.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {

    private final AuthController controller = new AuthController();

    @Test
    void deberiaRetornarLoginAdminCorrectamente() {
        LoginRequest request = new LoginRequest("admin@test.com", "1234");

        ResponseEntity<LoginResponse> response = controller.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("admin", response.getBody().getRol());
        assertNotNull(response.getBody().getToken());
    }

    @Test
    void deberiaRetornarBadRequestCuandoFaltanDatos() {
        LoginRequest request = new LoginRequest("", "");

        ResponseEntity<LoginResponse> response = controller.login(request);

        assertEquals(400, response.getStatusCode().value());
    }
}
