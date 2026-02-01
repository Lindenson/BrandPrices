package com.wolper.prices.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para el controlador de precios.
 * Valida los 5 escenarios solicitados en el enunciado.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PriceControllerIT {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @DisplayName("Test 1: petición a las 10:00 del día 14 del producto 35455 para la brand 1 (ZARA)")
    void test1_At10amOn14th_ShouldReturnPriceList1() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", "2020-06-14T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50))
                .andExpect(jsonPath("$.curr").value("EUR"))
                .andExpect(jsonPath("$.startDate").value("2020-06-14T00:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-12-31T23:59:59"));
    }
    
    @Test
    @DisplayName("Test 2: petición a las 16:00 del día 14 del producto 35455 para la brand 1 (ZARA)")
    void test2_At4pmOn14th_ShouldReturnPriceList2() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", "2020-06-14T16:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(2))
                .andExpect(jsonPath("$.price").value(25.45))
                .andExpect(jsonPath("$.curr").value("EUR"))
                .andExpect(jsonPath("$.startDate").value("2020-06-14T15:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-06-14T18:30:00"));
    }
    
    @Test
    @DisplayName("Test 3: petición a las 21:00 del día 14 del producto 35455 para la brand 1 (ZARA)")
    void test3_At9pmOn14th_ShouldReturnPriceList1() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", "2020-06-14T21:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50))
                .andExpect(jsonPath("$.curr").value("EUR"))
                .andExpect(jsonPath("$.startDate").value("2020-06-14T00:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-12-31T23:59:59"));
    }
    
    @Test
    @DisplayName("Test 4: petición a las 10:00 del día 15 del producto 35455 para la brand 1 (ZARA)")
    void test4_At10amOn15th_ShouldReturnPriceList3() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", "2020-06-15T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(3))
                .andExpect(jsonPath("$.price").value(30.50))
                .andExpect(jsonPath("$.curr").value("EUR"))
                .andExpect(jsonPath("$.startDate").value("2020-06-15T00:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-06-15T11:00:00"));
    }
    
    @Test
    @DisplayName("Test 5: petición a las 21:00 del día 16 del producto 35455 para la brand 1 (ZARA)")
    void test5_At9pmOn16th_ShouldReturnPriceList4() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", "2020-06-16T21:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(4))
                .andExpect(jsonPath("$.price").value(38.95))
                .andExpect(jsonPath("$.curr").value("EUR"))
                .andExpect(jsonPath("$.startDate").value("2020-06-15T16:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-12-31T23:59:59"));
    }
    
    @Test
    @DisplayName("Test de validación: debe retornar 400 cuando falta parámetro requerido")
    void testMissingParameter_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", "2020-06-14T10:00:00")
                        .param("productId", "35455")
                        // Falta brandId
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Test de validación: debe retornar 400 cuando productId es inválido")
    void testInvalidProductId_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", "2020-06-14T10:00:00")
                        .param("productId", "0") // Inválido: debe ser >= 1
                        .param("brandId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
    
    @Test
    @DisplayName("Test de excepción: debe retornar 404 cuando no se encuentra precio")
    void testPriceNotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", "2025-01-01T10:00:00") // Fecha fuera de rango
                        .param("productId", "35455")
                        .param("brandId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    @DisplayName("Test de headers: debe incluir X-Request-ID y Cache-Control")
    void testResponseHeaders() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", "2020-06-14T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-ID"))
                .andExpect(header().string("Cache-Control", "no-cache"));
    }
}
