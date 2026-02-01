package com.wolper.prices.application.service;

import com.wolper.prices.application.port.out.PriceRepository;
import com.wolper.prices.domain.exception.PriceNotFoundException;
import com.wolper.prices.domain.model.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para GetFinalPriceService.
 */
@ExtendWith(MockitoExtension.class)
class GetFinalPriceServiceTest {
    
    @Mock
    private PriceRepository priceRepository;
    
    private GetFinalPriceService service;
    
    @BeforeEach
    void setUp() {
        service = new GetFinalPriceService(priceRepository);
    }
    
    @Test
    void shouldReturnPriceWhenFound() {
        // Given
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
        Long productId = 35455L;
        Long brandId = 1L;
        
        Price expectedPrice = Price.builder()
                .id(1L)
                .brandId(brandId)
                .productId(productId)
                .priceList(1L)
                .priority(0)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .build();
        
        when(priceRepository.findApplicablePrices(eq(applicationDate), eq(productId), eq(brandId)))
                .thenReturn(List.of(expectedPrice));
        
        // When
        Price result = service.getFinalPrice(applicationDate, productId, brandId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getBrandId()).isEqualTo(brandId);
        assertThat(result.getPriceList()).isEqualTo(1L);
        assertThat(result.getPrice()).isEqualByComparingTo("35.50");
    }
    
    @Test
    void shouldReturnHighestPriorityPriceWhenMultipleFound() {
        // Given
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 16, 0);
        Long productId = 35455L;
        Long brandId = 1L;
        
        Price highPriorityPrice = Price.builder()
                .id(2L)
                .priority(1)
                .price(new BigDecimal("25.45"))
                .priceList(2L)
                .build();
        
        Price lowPriorityPrice = Price.builder()
                .id(1L)
                .priority(0)
                .price(new BigDecimal("35.50"))
                .priceList(1L)
                .build();
        
        // El repositorio ya devuelve ordenado por prioridad DESC
        when(priceRepository.findApplicablePrices(any(), any(), any()))
                .thenReturn(List.of(highPriorityPrice, lowPriorityPrice));
        
        // When
        Price result = service.getFinalPrice(applicationDate, productId, brandId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPriority()).isEqualTo(1);
        assertThat(result.getPrice()).isEqualByComparingTo("25.45");
    }
    
    @Test
    void shouldThrowPriceNotFoundExceptionWhenNoPriceFound() {
        // Given
        LocalDateTime applicationDate = LocalDateTime.of(2025, 1, 1, 10, 0);
        Long productId = 99999L;
        Long brandId = 1L;
        
        when(priceRepository.findApplicablePrices(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        
        // When & Then
        assertThatThrownBy(() -> service.getFinalPrice(applicationDate, productId, brandId))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessageContaining("productId=99999")
                .hasMessageContaining("brandId=1");
    }
}
