package com.wolper.prices.adapter.out.persistence;

import com.wolper.prices.domain.model.BrandPrice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integración para el repositorio JDBC.
 */
@SpringBootTest
class JdbcPriceRepositoryIT {
    
    @Autowired
    private JdbcPriceRepository repository;
    
    @Test
    void shouldFindApplicablePrices() {
        // Given
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
        Long productId = 35455L;
        Long brandId = 1L;
        
        // When
        List<BrandPrice> prices = repository.findApplicablePrices(applicationDate, productId, brandId);
        
        // Then
        assertThat(prices).isNotEmpty().hasSize(1); // Solo un precio aplicable a las 10:00
        assertThat(prices.getFirst().getPriceList()).isEqualTo(1L);
    }
    
    @Test
    void shouldReturnPricesOrderedByPriority() {
        // Given
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 16, 0);
        Long productId = 35455L;
        Long brandId = 1L;
        
        // When
        List<BrandPrice> prices = repository.findApplicablePrices(applicationDate, productId, brandId);
        
        // Then
        assertThat(prices).hasSize(2); // Dos precios aplicables
        // El primero debe ser el de mayor prioridad
        assertThat(prices.get(0).getPriority()).isGreaterThan(prices.get(1).getPriority());
        assertThat(prices.get(0).getPriceList()).isEqualTo(2L);
    }
    
    @Test
    void shouldReturnEmptyListWhenNoPriceFound() {
        // Given
        LocalDateTime applicationDate = LocalDateTime.of(2025, 1, 1, 10, 0);
        Long productId = 35455L;
        Long brandId = 1L;
        
        // When
        List<BrandPrice> prices = repository.findApplicablePrices(applicationDate, productId, brandId);
        
        // Then
        assertThat(prices).isEmpty();
    }
    
    @Test
    void shouldFindPriceAtExactStartDate() {
        // Given
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 0, 0);
        Long productId = 35455L;
        Long brandId = 1L;
        
        // When
        List<BrandPrice> prices = repository.findApplicablePrices(applicationDate, productId, brandId);
        
        // Then
        assertThat(prices).isNotEmpty();
    }
    
    @Test
    void shouldFindPriceAtExactEndDate() {
        // Given
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 18, 30);
        Long productId = 35455L;
        Long brandId = 1L;
        
        // When
        List<BrandPrice> prices = repository.findApplicablePrices(applicationDate, productId, brandId);
        
        // Then
        assertThat(prices).isNotEmpty().anyMatch(p -> p.getPriceList().equals(2L));
    }
}
