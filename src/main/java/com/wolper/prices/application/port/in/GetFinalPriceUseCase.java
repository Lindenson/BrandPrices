package com.wolper.prices.application.port.in;

import com.wolper.prices.domain.model.BrandPrice;

import java.time.LocalDateTime;

/**
 * Puerto de entrada para el caso de uso de obtener el precio final.
 * Define el contrato que debe cumplir cualquier implementación.
 */
public interface GetFinalPriceUseCase {
    
    /**
     * Obtiene el precio final aplicable para un producto en una fecha específica.
     * 
     * @param applicationDate Fecha de aplicación del precio
     * @param productId Identificador del producto
     * @param brandId Identificador de la marca
     * @return El precio aplicable con mayor prioridad
     * @throws com.wolper.prices.domain.exception.PriceNotFoundException si no se encuentra precio aplicable
     */
    BrandPrice getFinalPrice(LocalDateTime applicationDate, Long productId, Long brandId);
}
