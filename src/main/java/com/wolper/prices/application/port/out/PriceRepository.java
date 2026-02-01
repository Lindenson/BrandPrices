package com.wolper.prices.application.port.out;

import com.wolper.prices.domain.model.Price;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Puerto de salida para la persistencia de precios.
 * Define el contrato que debe implementar cualquier adaptador de persistencia.
 */
public interface PriceRepository {
    
    /**
     * Busca todos los precios aplicables para un producto y marca en una fecha específica.
     * 
     * @param applicationDate Fecha de aplicación
     * @param productId Identificador del producto
     * @param brandId Identificador de la marca
     * @return Lista de precios aplicables ordenados por prioridad (mayor a menor)
     */
    List<Price> findApplicablePrices(LocalDateTime applicationDate, Long productId, Long brandId);
}
