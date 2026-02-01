package com.wolper.prices.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad de dominio que representa un precio aplicable a un producto
 * en un rango de fechas específico.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Price {
    
    private Long id;
    private Long brandId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long priceList;
    private Long productId;
    private Integer priority;
    private BigDecimal price;
    private String currency;
    
    /**
     * Verifica si este precio es aplicable en la fecha especificada
     */
    public boolean isApplicableAt(LocalDateTime date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
    
    /**
     * Compara prioridades. Retorna true si este precio tiene mayor prioridad
     */
    public boolean hasHigherPriorityThan(Price other) {
        if (other == null) {
            return true;
        }
        return this.priority > other.priority;
    }
}
