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
public class DistribucionDetalleResponse {
    private Long id;
    private String destino;
    private String transporte;
    private String estado;
    private String observaciones;
    private LocalDateTime fechaPlanificacion;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaEntrega;

    private DonacionInfo donacion;
    private NecesidadInfo necesidad;
    private CentroAcopioInfo centroAcopio;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DonacionInfo {
        private Long id;
        private String tipoRecurso;
        private String detalleRecurso;
        private Integer cantidad;
        private String nombreDonante;
        private String contactoDonante;
        private String estado;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class NecesidadInfo {
        private Long id;
        private String recursoNecesitado;
        private Integer cantidad;
        private String ubicacion;
        private String prioridad;
        private String estado;
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
