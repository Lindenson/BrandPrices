package com.wolper.prices.adapter.in.web;

import com.wolper.prices.adapter.in.web.dto.PriceResponse;
import com.wolper.prices.adapter.in.web.mapper.PriceMapper;
import com.wolper.prices.application.port.in.GetFinalPriceUseCase;
import com.wolper.prices.domain.model.Price;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Controlador REST para consultas de precios.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
@Tag(name = "Prices", description = "API de consulta de precios")
public class PriceController {
    
    private final GetFinalPriceUseCase getFinalPriceUseCase;
    private final PriceMapper priceMapper;
    
    @Operation(
        summary = "Obtener precio final",
        description = "Retorna el precio aplicable para un producto en una marca específica en una fecha determinada"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Precio encontrado exitosamente",
            content = @Content(schema = @Schema(implementation = PriceResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Parámetros inválidos"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Precio no encontrado"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    @GetMapping("/final")
    public ResponseEntity<PriceResponse> getFinalPrice(
            @Parameter(description = "Fecha de aplicación del precio (ISO 8601)", required = true, example = "2020-06-14T10:00:00")
            @RequestParam
            @NotNull(message = "La fecha es obligatoria")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime date,
            
            @Parameter(description = "Identificador del producto", required = true, example = "35455")
            @RequestParam
            @NotNull(message = "El productId es obligatorio")
            @Min(value = 1, message = "El productId debe ser mayor que 0")
            Long productId,
            
            @Parameter(description = "Identificador de la marca", required = true, example = "1")
            @RequestParam
            @NotNull(message = "El brandId es obligatorio")
            @Min(value = 1, message = "El brandId debe ser mayor que 0")
            Long brandId
    ) {
        String requestId = UUID.randomUUID().toString();
        log.info("[{}] GET /prices/final - date={}, productId={}, brandId={}", 
                 requestId, date, productId, brandId);
        
        Price price = getFinalPriceUseCase.getFinalPrice(date, productId, brandId);
        PriceResponse response = priceMapper.toResponse(price);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Request-ID", requestId);
        headers.add("Cache-Control", "no-cache");
        
        log.info("[{}] Precio encontrado: priceList={}, price={}", 
                 requestId, response.getPriceList(), response.getPrice());
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(response);
    }
}
