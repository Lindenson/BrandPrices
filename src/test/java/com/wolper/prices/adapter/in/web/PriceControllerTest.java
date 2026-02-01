package com.wolper.prices.adapter.in.web;

import com.wolper.prices.adapter.in.web.dto.PriceResponse;
import com.wolper.prices.adapter.in.web.mapper.PriceMapper;
import com.wolper.prices.application.port.in.GetFinalPriceUseCase;
import com.wolper.prices.domain.exception.PriceNotFoundException;
import com.wolper.prices.domain.model.Price;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PriceController.class)
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetFinalPriceUseCase getFinalPriceUseCase;

    @MockitoBean
    private PriceMapper priceMapper;

    // =====================
    // 200 OK
    // =====================
    @Test
    void testGetFinalPriceReturnsPrice() throws Exception {
        LocalDateTime date = LocalDateTime.of(2026, 1, 9, 10, 0);

        Price price = getPriceForTest(date);
        PriceResponse response = new PriceResponse(
                35455L, 1L, 1L, date.minusDays(1),
                date.plusDays(1), BigDecimal.valueOf(35.50), "EUR"
        );

        when(getFinalPriceUseCase.getFinalPrice(date, 35455L, 1L))
                .thenReturn(price);

        when(priceMapper.toResponse(price)).thenReturn(response);

        mockMvc.perform(get("/prices/final")
                        .param("date", date.toString())
                        .param("productId", "35455")
                        .param("brandId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.price").value(35.50));
    }

    // =====================
    // 404 Not Found
    // =====================
    @Test
    void testGetFinalPriceReturns404WhenNotFound() throws Exception {
        LocalDateTime date = LocalDateTime.of(2026, 1, 9, 10, 0);

        when(getFinalPriceUseCase.getFinalPrice(date, 35455L, 1L))
                .thenThrow(new PriceNotFoundException(0L, 0L, "Price not found"));

        mockMvc.perform(get("/prices/final")
                        .param("date", date.toString())
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isNotFound());
    }

    // =====================
    // 400 Bad Request — missing param
    // =====================
    @Test
    void testGetFinalPriceValidationError() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", "2026-01-09T10:00:00")
                        .param("productId", "35455"))
                .andExpect(status().isBadRequest());
    }

    // =====================
    // 400 Bad Request — type mismatch
    // =====================
    @Test
    void testGetFinalPriceTypeMismatch() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", "2026-01-09T10:00:00")
                        .param("productId", "abc")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest());
    }

    // =====================
    // 400 Bad Request — constraint violation
    // =====================
    @Test
    void testGetFinalPriceConstraintViolation() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", "2026-01-09T10:00:00")
                        .param("productId", "-1")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest());
    }

    // =====================
    // 500 Internal Server Error
    // =====================
    @Test
    void testGetFinalPriceThrowsGenericException() throws Exception {
        LocalDateTime date = LocalDateTime.of(2026, 1, 9, 10, 0);

        when(getFinalPriceUseCase.getFinalPrice(date, 35455L, 1L))
                .thenThrow(new RuntimeException("Unexpected"));

        mockMvc.perform(get("/prices/final")
                        .param("date", date.toString())
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isInternalServerError());
    }

    // helper
    private static Price getPriceForTest(LocalDateTime date) {
        return Price.builder()
                .brandId(1L)
                .productId(35455L)
                .priceList(1L)
                .startDate(date.minusDays(1))
                .endDate(date.plusDays(1))
                .price(BigDecimal.valueOf(35.50))
                .currency("EUR")
                .priority(0)
                .build();
    }
}
