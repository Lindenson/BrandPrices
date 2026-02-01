package com.wolper.prices.application.service;

import com.wolper.prices.application.port.in.GetFinalPriceUseCase;
import com.wolper.prices.application.port.out.PriceRepository;
import com.wolper.prices.domain.exception.PriceNotFoundException;
import com.wolper.prices.domain.model.Price;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de aplicación que implementa la lógica de negocio
 * para obtener el precio final aplicable.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetFinalPriceService implements GetFinalPriceUseCase {
    
    private final PriceRepository priceRepository;
    
    @Override
    public Price getFinalPrice(LocalDateTime applicationDate, Long productId, Long brandId) {
        log.debug("Buscando precio para productId={}, brandId={}, fecha={}", 
                  productId, brandId, applicationDate);
        
        List<Price> applicablePrices = priceRepository.findApplicablePrices(
                applicationDate, productId, brandId);
        
        if (applicablePrices.isEmpty()) {
            log.warn("No se encontró precio para productId={}, brandId={}, fecha={}", 
                     productId, brandId, applicationDate);
            throw new PriceNotFoundException(productId, brandId, applicationDate.toString());
        }
        
        // El repositorio ya retorna los precios ordenados por prioridad descendente
        // por lo que el primero es el de mayor prioridad
        Price finalPrice = applicablePrices.get(0);
        
        log.info("Precio encontrado: priceList={}, price={} {}", 
                 finalPrice.getPriceList(), finalPrice.getPrice(), finalPrice.getCurrency());
        
        return finalPrice;
    }
}
