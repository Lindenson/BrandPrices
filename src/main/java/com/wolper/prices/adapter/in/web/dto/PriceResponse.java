package com.wolper.prices.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para el endpoint de precio final.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con el precio final aplicable")
public class PriceResponse {
    
    @Schema(description = "Identificador del producto", example = "35455")
    private Long productId;
    
    @Schema(description = "Identificador de la marca", example = "1")
    private Long brandId;
    
    @Schema(description = "Identificador de la tarifa aplicable", example = "1")
    private Long priceList;
    
    @Schema(description = "Fecha de inicio de validez", example = "2020-06-14T00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    
    @Schema(description = "Fecha de fin de validez", example = "2020-12-31T23:59:59")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;
    
    @Schema(description = "Precio final de venta", example = "35.50")
    private BigDecimal price;
    
    @Schema(description = "Código ISO de la moneda", example = "EUR")
    private String curr;
}
