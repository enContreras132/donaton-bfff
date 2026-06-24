package com.example.donaton_bff.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DonacionDetalleResponse {
    private Long id;
    private String tipoRecurso;
    private String detalleRecurso;
    private Integer cantidad;
    private String origen;
    private String nombreDonante;
    private String contactoDonante;
    private String estado;
    private LocalDateTime fechaRegistro;

    private EnvioInfo envio;
    private CentroAcopioInfo centroAcopio;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EnvioInfo {
        private Long id;
        private String destino;
        private String transporte;
        private String estado;
        private LocalDateTime fechaPlanificacion;
        private LocalDateTime fechaSalida;
        private LocalDateTime fechaEntrega;
        private String observaciones;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CentroAcopioInfo {
        private Long id;
        private String nombre;
        private String direccion;
        private String comuna;
    }
}
