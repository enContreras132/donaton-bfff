package com.example.donaton_bff.service;

import com.example.donaton_bff.dto.response.DistribucionDetalleResponse;
import com.example.donaton_bff.dto.response.DonacionDetalleResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class BffService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    private final String donacionesUrl;
    private final String necesidadesUrl;
    private final String logisticaUrl;

    public BffService(RestClient restClient,
                      ObjectMapper objectMapper,
                      @Value("${services.donaciones.url}") String donacionesUrl,
                      @Value("${services.necesidades.url}") String necesidadesUrl,
                      @Value("${services.logistica.url}") String logisticaUrl) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.donacionesUrl = donacionesUrl;
        this.necesidadesUrl = necesidadesUrl;
        this.logisticaUrl = logisticaUrl;
    }

    public String proxyRequest(String method, String pathSuffix, String body) {
        String url = resolveUrl(pathSuffix);
        if (url == null) {
            throw new IllegalArgumentException("No se pudo resolver la URL para: " + pathSuffix);
        }

        HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());
        org.springframework.web.client.RestClient.RequestBodySpec spec = restClient.method(httpMethod).uri(url);
        if (body != null && !body.isBlank()
                && (httpMethod == HttpMethod.POST || httpMethod == HttpMethod.PUT || httpMethod == HttpMethod.PATCH)) {
            spec.contentType(MediaType.APPLICATION_JSON);
            spec.body(body);
        }

        ResponseEntity<String> response = spec.retrieve().toEntity(String.class);
        return response.getBody();
    }

    public <T> T proxyGetForObject(String pathSuffix, Class<T> responseType) {
        String url = resolveUrl(pathSuffix);
        return restClient.get().uri(url).retrieve().body(responseType);
    }

    public <T> List<T> proxyGetForList(String pathSuffix, Class<T> elementType) {
        String url = resolveUrl(pathSuffix);
        String json = restClient.get().uri(url).retrieve().body(String.class);
        return objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructCollectionType(List.class, elementType));
    }

    public <T> T proxyPost(String pathSuffix, Object requestBody, Class<T> responseType) {
        String url = resolveUrl(pathSuffix);
        return restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(responseType);
    }

    public <T> T proxyPut(String pathSuffix, Object requestBody, Class<T> responseType) {
        String url = resolveUrl(pathSuffix);
        return restClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(responseType);
    }

    public void proxyDelete(String pathSuffix) {
        String url = resolveUrl(pathSuffix);
        restClient.delete().uri(url).retrieve().toBodilessEntity();
    }

    public <T> T proxyPatch(String pathSuffix, Class<T> responseType) {
        String url = resolveUrl(pathSuffix);
        return restClient.patch()
                .uri(url)
                .retrieve()
                .body(responseType);
    }

    public DonacionDetalleResponse getDonacionDetalle(Long donacionId) {
        JsonNode donacion = proxyGetForObject("/donaciones/" + donacionId, JsonNode.class);
        if (donacion == null) return null;

        DonacionDetalleResponse response = new DonacionDetalleResponse();
        response.setId(donacion.has("id") ? donacion.get("id").asLong() : null);
        response.setTipoRecurso(donacion.has("tipoRecurso") ? donacion.get("tipoRecurso").asText() : null);
        response.setDetalleRecurso(donacion.has("detalleRecurso") ? donacion.get("detalleRecurso").asText() : null);
        response.setCantidad(donacion.has("cantidad") ? donacion.get("cantidad").asInt() : null);
        response.setOrigen(donacion.has("origen") ? donacion.get("origen").asText() : null);
        response.setNombreDonante(donacion.has("nombreDonante") ? donacion.get("nombreDonante").asText(null) : null);
        response.setContactoDonante(donacion.has("contactoDonante") ? donacion.get("contactoDonante").asText(null) : null);
        response.setEstado(donacion.has("estado") ? donacion.get("estado").asText() : null);
        response.setFechaRegistro(donacion.has("fechaRegistro") ? parseDateTime(donacion.get("fechaRegistro").asText()) : null);

        Long centroAcopioId = donacion.has("centroAcopioId") && !donacion.get("centroAcopioId").isNull()
                ? donacion.get("centroAcopioId").asLong() : null;

        try {
            List<JsonNode> envios = proxyGetForList("/envios/donacion/" + donacionId, JsonNode.class);
            if (envios != null && !envios.isEmpty()) {
                JsonNode envio = envios.get(envios.size() - 1);
                DonacionDetalleResponse.EnvioInfo envioInfo = DonacionDetalleResponse.EnvioInfo.builder()
                        .id(envio.has("id") ? envio.get("id").asLong() : null)
                        .destino(envio.has("destino") ? envio.get("destino").asText() : null)
                        .transporte(envio.has("transporte") ? envio.get("transporte").asText() : null)
                        .estado(envio.has("estado") ? envio.get("estado").asText() : null)
                        .fechaPlanificacion(envio.has("fechaPlanificacion") ? parseDateTime(envio.get("fechaPlanificacion").asText()) : null)
                        .fechaSalida(envio.has("fechaSalida") && !envio.get("fechaSalida").isNull() ? parseDateTime(envio.get("fechaSalida").asText()) : null)
                        .fechaEntrega(envio.has("fechaEntrega") && !envio.get("fechaEntrega").isNull() ? parseDateTime(envio.get("fechaEntrega").asText()) : null)
                        .observaciones(envio.has("observaciones") ? envio.get("observaciones").asText(null) : null)
                        .build();
                response.setEnvio(envioInfo);
            }
        } catch (Exception ignored) {}

        if (centroAcopioId != null) {
            try {
                JsonNode centro = proxyGetForObject("/centros-acopio/" + centroAcopioId, JsonNode.class);
                if (centro != null) {
                    DonacionDetalleResponse.CentroAcopioInfo centroInfo = DonacionDetalleResponse.CentroAcopioInfo.builder()
                            .id(centro.has("id") ? centro.get("id").asLong() : null)
                            .nombre(centro.has("nombre") ? centro.get("nombre").asText() : null)
                            .direccion(centro.has("direccion") ? centro.get("direccion").asText() : null)
                            .comuna(centro.has("comuna") ? centro.get("comuna").asText() : null)
                            .build();
                    response.setCentroAcopio(centroInfo);
                }
            } catch (Exception ignored) {}
        }

        return response;
    }

    public DistribucionDetalleResponse getDistribucionDetalle(Long envioId) {
        JsonNode envio = proxyGetForObject("/envios/" + envioId, JsonNode.class);
        if (envio == null) return null;

        DistribucionDetalleResponse response = new DistribucionDetalleResponse();
        response.setId(envio.has("id") ? envio.get("id").asLong() : null);
        response.setDestino(envio.has("destino") ? envio.get("destino").asText() : null);
        response.setTransporte(envio.has("transporte") ? envio.get("transporte").asText() : null);
        response.setEstado(envio.has("estado") ? envio.get("estado").asText() : null);
        response.setObservaciones(envio.has("observaciones") ? envio.get("observaciones").asText(null) : null);
        response.setFechaPlanificacion(envio.has("fechaPlanificacion") ? parseDateTime(envio.get("fechaPlanificacion").asText()) : null);
        response.setFechaSalida(envio.has("fechaSalida") && !envio.get("fechaSalida").isNull() ? parseDateTime(envio.get("fechaSalida").asText()) : null);
        response.setFechaEntrega(envio.has("fechaEntrega") && !envio.get("fechaEntrega").isNull() ? parseDateTime(envio.get("fechaEntrega").asText()) : null);

        Long donacionId = envio.has("donacionId") && !envio.get("donacionId").isNull()
                ? envio.get("donacionId").asLong() : null;
        Long necesidadId = envio.has("necesidadId") && !envio.get("necesidadId").isNull()
                ? envio.get("necesidadId").asLong() : null;
        Long centroAcopioId = envio.has("centroAcopioId") && !envio.get("centroAcopioId").isNull()
                ? envio.get("centroAcopioId").asLong() : null;

        if (donacionId != null) {
            try {
                JsonNode donacion = proxyGetForObject("/donaciones/" + donacionId, JsonNode.class);
                if (donacion != null) {
                    DistribucionDetalleResponse.DonacionInfo donacionInfo = DistribucionDetalleResponse.DonacionInfo.builder()
                            .id(donacion.has("id") ? donacion.get("id").asLong() : null)
                            .tipoRecurso(donacion.has("tipoRecurso") ? donacion.get("tipoRecurso").asText() : null)
                            .detalleRecurso(donacion.has("detalleRecurso") ? donacion.get("detalleRecurso").asText() : null)
                            .cantidad(donacion.has("cantidad") ? donacion.get("cantidad").asInt() : null)
                            .nombreDonante(donacion.has("nombreDonante") ? donacion.get("nombreDonante").asText(null) : null)
                            .contactoDonante(donacion.has("contactoDonante") ? donacion.get("contactoDonante").asText(null) : null)
                            .estado(donacion.has("estado") ? donacion.get("estado").asText() : null)
                            .build();
                    response.setDonacion(donacionInfo);
                }
            } catch (Exception ignored) {}
        }

        if (necesidadId != null) {
            try {
                JsonNode necesidad = proxyGetForObject("/necesidades/" + necesidadId, JsonNode.class);
                if (necesidad != null) {
                    DistribucionDetalleResponse.NecesidadInfo necesidadInfo = DistribucionDetalleResponse.NecesidadInfo.builder()
                            .id(necesidad.has("id") ? necesidad.get("id").asLong() : null)
                            .recursoNecesitado(necesidad.has("recursoNecesitado") ? necesidad.get("recursoNecesitado").asText() : null)
                            .cantidad(necesidad.has("cantidad") ? necesidad.get("cantidad").asInt() : null)
                            .ubicacion(necesidad.has("ubicacion") ? necesidad.get("ubicacion").asText() : null)
                            .prioridad(necesidad.has("prioridad") ? necesidad.get("prioridad").asText() : null)
                            .estado(necesidad.has("estado") ? necesidad.get("estado").asText() : null)
                            .build();
                    response.setNecesidad(necesidadInfo);
                }
            } catch (Exception ignored) {}
        }

        if (centroAcopioId != null) {
            try {
                JsonNode centro = proxyGetForObject("/centros-acopio/" + centroAcopioId, JsonNode.class);
                if (centro != null) {
                    DistribucionDetalleResponse.CentroAcopioInfo centroInfo = DistribucionDetalleResponse.CentroAcopioInfo.builder()
                            .id(centro.has("id") ? centro.get("id").asLong() : null)
                            .nombre(centro.has("nombre") ? centro.get("nombre").asText() : null)
                            .direccion(centro.has("direccion") ? centro.get("direccion").asText() : null)
                            .comuna(centro.has("comuna") ? centro.get("comuna").asText() : null)
                            .build();
                    response.setCentroAcopio(centroInfo);
                }
            } catch (Exception ignored) {}
        }

        return response;
    }

    public JsonNode normalizarCrearDonacion(JsonNode body) {
        ObjectNode normalizada = objectMapper.createObjectNode();

        String tipoRecurso = body.has("tipoRecurso") && !body.get("tipoRecurso").asText().isBlank()
                ? body.get("tipoRecurso").asText() : "OTRO";
        normalizada.put("tipoRecurso", tipoRecurso.toUpperCase());

        String detalleRecurso;
        if (body.has("detalleRecurso") && !body.get("detalleRecurso").isNull() && !body.get("detalleRecurso").asText().isBlank()) {
            detalleRecurso = body.get("detalleRecurso").asText();
        } else if (body.has("detalles")) {
            JsonNode detalles = body.get("detalles");
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, JsonNode> field : detalles.properties()) {
                String name = field.getKey();
                if ("donador".equals(name) || "email".equals(name) || "mensaje".equals(name)) continue;
                sb.append(field.getValue().asText()).append(" - ");
            }
            if (body.has("unidad") && !body.get("unidad").isNull()) {
                sb.append("Unidad: ").append(body.get("unidad").asText()).append(" - ");
            }
            detalleRecurso = sb.length() > 3 ? sb.substring(0, sb.length() - 3) : "Sin detalle";
        } else if (body.has("recurso")) {
            detalleRecurso = body.get("recurso").asText();
        } else {
            detalleRecurso = "Sin detalle";
        }
        normalizada.put("detalleRecurso", detalleRecurso);

        int cantidad;
        if (body.has("cantidad")) {
            cantidad = body.get("cantidad").asInt();
        } else if (body.has("detalles") && body.get("detalles").has("cantidad")) {
            cantidad = body.get("detalles").get("cantidad").asInt();
        } else {
            cantidad = 1;
        }
        normalizada.put("cantidad", cantidad);

        if (body.has("origen") && !body.get("origen").isNull() && !body.get("origen").asText().isBlank()) {
            normalizada.put("origen", body.get("origen").asText());
        } else if (body.has("detalles") && body.get("detalles").has("donador")) {
            normalizada.put("origen", body.get("detalles").get("donador").asText());
        } else if (body.has("donador")) {
            normalizada.put("origen", body.get("donador").asText());
        } else if (body.has("nombreDonante")) {
            normalizada.put("origen", body.get("nombreDonante").asText());
        } else {
            normalizada.put("origen", "Anónimo");
        }

        if (body.has("nombreDonante") && !body.get("nombreDonante").isNull()) {
            normalizada.put("nombreDonante", body.get("nombreDonante").asText());
        } else if (body.has("detalles") && body.get("detalles").has("donador")) {
            normalizada.put("nombreDonante", body.get("detalles").get("donador").asText());
        } else if (body.has("donador")) {
            normalizada.put("nombreDonante", body.get("donador").asText());
        } else {
            normalizada.putNull("nombreDonante");
        }

        if (body.has("contactoDonante") && !body.get("contactoDonante").isNull()) {
            normalizada.put("contactoDonante", body.get("contactoDonante").asText());
        } else if (body.has("detalles") && body.get("detalles").has("email")) {
            normalizada.put("contactoDonante", body.get("detalles").get("email").asText());
        } else if (body.has("email")) {
            normalizada.put("contactoDonante", body.get("email").asText());
        } else {
            normalizada.putNull("contactoDonante");
        }

        if (body.has("centroAcopioId") && !body.get("centroAcopioId").isNull()) {
            normalizada.put("centroAcopioId", body.get("centroAcopioId").asLong());
        } else {
            normalizada.putNull("centroAcopioId");
        }

        if (body.has("necesidadId") && !body.get("necesidadId").isNull()) {
            normalizada.put("necesidadId", body.get("necesidadId").asLong());
        } else {
            normalizada.putNull("necesidadId");
        }

        return normalizada;
    }

    private String resolveUrl(String pathSuffix) {
        if (pathSuffix.startsWith("/donaciones")) {
            return donacionesUrl + pathSuffix.substring("/donaciones".length());
        } else if (pathSuffix.startsWith("/necesidades")) {
            return necesidadesUrl + pathSuffix.substring("/necesidades".length());
        } else if (pathSuffix.startsWith("/envios")) {
            return logisticaUrl + pathSuffix;
        } else if (pathSuffix.startsWith("/centros-acopio")) {
            return logisticaUrl + pathSuffix;
        } else if (pathSuffix.startsWith("/logistica")) {
            return logisticaUrl + pathSuffix.substring("/logistica".length());
        }
        return null;
    }

    private java.time.LocalDateTime parseDateTime(String text) {
        if (text == null || text.isBlank()) return null;
        try {
            return java.time.LocalDateTime.parse(text);
        } catch (Exception e) {
            try {
                return java.time.LocalDateTime.parse(text, java.time.format.DateTimeFormatter.ISO_DATE_TIME);
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
