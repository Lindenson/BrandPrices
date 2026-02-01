package com.wolper.prices.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para respuestas de error.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Respuesta de error")
public class ErrorResponse {
    
    @Schema(description = "Timestamp del error", example = "2026-01-31T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime timestamp;
    
    @Schema(description = "Código de estado HTTP", example = "400")
    private Integer status;
    
    @Schema(description = "Descripción del error HTTP", example = "Bad Request")
    private String error;
    
    @Schema(description = "Mensaje de error", example = "Parámetros inválidos")
    private String message;
    
    @Schema(description = "Path del endpoint", example = "/prices/final")
    private String path;
    
    @Schema(description = "Errores de validación por campo")
    private Map<String, String> fields;
}
