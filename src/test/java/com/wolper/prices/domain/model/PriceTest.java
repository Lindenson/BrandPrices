package com.wolper.prices.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para la entidad Price.
 */
class PriceTest {
    
    @Test
    void shouldCreatePrice() {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 12, 31, 23, 59);
        
        // When
        BrandPrice price = BrandPrice.builder()
                .id(1L)
                .brandId(1L)
                .startDate(startDate)
                .endDate(endDate)
                .priceList(1L)
                .productId(35455L)
                .priority(0)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .build();
        
        // Then
        assertThat(price).isNotNull();
        assertThat(price.getBrandId()).isEqualTo(1L);
        assertThat(price.getProductId()).isEqualTo(35455L);
        assertThat(price.getPrice()).isEqualByComparingTo("35.50");
    }
    
    @Test
    void shouldBeApplicableWhenDateIsWithinRange() {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 12, 31, 23, 59);
        LocalDateTime testDate = LocalDateTime.of(2020, 6, 14, 10, 0);
        
        BrandPrice price = BrandPrice.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
        
        // When
        boolean applicable = price.isApplicableAt(testDate);
        
        // Then
        assertThat(applicable).isTrue();
    }
    
    @Test
    void shouldNotBeApplicableWhenDateIsBeforeStart() {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 15, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 6, 14, 18, 30);
        LocalDateTime testDate = LocalDateTime.of(2020, 6, 14, 10, 0);
        
        BrandPrice price = BrandPrice.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
        
        // When
        boolean applicable = price.isApplicableAt(testDate);
        
        // Then
        assertThat(applicable).isFalse();
    }
    
    @Test
    void shouldNotBeApplicableWhenDateIsAfterEnd() {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 15, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 6, 14, 18, 30);
        LocalDateTime testDate = LocalDateTime.of(2020, 6, 14, 21, 0);
        
        BrandPrice price = BrandPrice.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
        
        // When
        boolean applicable = price.isApplicableAt(testDate);
        
        // Then
        assertThat(applicable).isFalse();
    }
    
    @Test
    void shouldHaveHigherPriorityWhenPriorityIsGreater() {
        // Given
        BrandPrice highPriority = BrandPrice.builder().priority(1).build();
        BrandPrice lowPriority = BrandPrice.builder().priority(0).build();
        
        // When & Then
        assertThat(highPriority.hasHigherPriorityThan(lowPriority)).isTrue();
        assertThat(lowPriority.hasHigherPriorityThan(highPriority)).isFalse();
    }
    
    @Test
    void shouldHaveHigherPriorityWhenOtherIsNull() {
        // Given
        BrandPrice price = BrandPrice.builder().priority(0).build();
        
        // When & Then
        assertThat(price.hasHigherPriorityThan(null)).isTrue();
    }
}
