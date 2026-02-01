package com.wolper.prices.adapter.in.web;

import com.wolper.prices.adapter.in.web.dto.ErrorResponse;
import com.wolper.prices.adapter.in.web.exception.GlobalExceptionHandler;
import com.wolper.prices.domain.exception.PriceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest httpServletRequest;


    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        httpServletRequest = mock(HttpServletRequest.class);
    }

    // ---------------------------------------------------------------
    // 1) ConstraintViolationException
    // ---------------------------------------------------------------
    @Test
    void testHandleConstraintViolation() {
        var violation = getViolationMocked();
        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex, httpServletRequest);

        assertEquals(400, response.getStatusCode().value());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Bad Request", body.getError());
        assertEquals("Error de validación", body.getMessage());
        assertEquals("must not be null", body.getFields().get("productId"));

        verify(violation, times(2)).getPropertyPath();
        verify(violation, times(2)).getMessage();
    }

    // ---------------------------------------------------------------
    // 2) MissingServletRequestParameterException
    // ---------------------------------------------------------------
    @Test
    void testHandleMissingParams() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("param", "String");

        ResponseEntity<ErrorResponse> response = handler.handleMissingParameter(ex, httpServletRequest);

        assertEquals(400, response.getStatusCode().value());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Bad Request", body.getError());
        assertEquals("Parámetro requerido 'param' no está presente", body.getMessage());
    }

    // ---------------------------------------------------------------
    // 3) MethodArgumentTypeMismatchException
    // ---------------------------------------------------------------
    @Test
    void testHandleTypeMismatchWithType() {
        MethodParameter methodParameter = mock(MethodParameter.class);

        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException("abc", Integer.class, "param", methodParameter, null);

        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex, httpServletRequest);

        assertEquals(400, response.getStatusCode().value());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("El parámetro 'param' debe ser de tipo Integer", body.getMessage());
        assertEquals("Bad Request", body.getError());
    }

    @Test
    void testHandleTypeMismatchWithoutType() {
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException("abc", null, "param", null, null);

        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex, httpServletRequest);

        assertEquals(400, response.getStatusCode().value());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("El parámetro 'param' debe ser de tipo desconocido", body.getMessage());
        assertEquals("Bad Request", body.getError());
    }

    // ---------------------------------------------------------------
    // 4) PriceNotFoundException
    // ---------------------------------------------------------------
    @Test
    void testHandlePriceNotFound() {
        PriceNotFoundException ex = new PriceNotFoundException(0L, 0L, "Price not found");

        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/prices/final");

        ResponseEntity<ErrorResponse> response = handler.handlePriceNotFound(ex, req);

        assertEquals(404, response.getStatusCode().value());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("No se encontró precio para productId=0, brandId=0, fecha=Price not found", body.getMessage());
        assertEquals("/prices/final", body.getPath());
    }

    // ---------------------------------------------------------------
    // 5) MethodArgumentNotValidException
    // ---------------------------------------------------------------
    @Test
    void testHandleMethodArgumentNotValid() {

        FieldError fieldError = new FieldError("req", "productId", "must not be null");

        BindingResult binding = mock(BindingResult.class);
        when(binding.getFieldErrors()).thenReturn(List.of(fieldError));

        Method executable = mock(Method.class);

        MethodParameter methodParameter = mock(MethodParameter.class);
        when(methodParameter.getExecutable()).thenReturn(executable);

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(methodParameter, binding);

        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/prices/final");

        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentNotValid(ex, req);

        assertEquals(400, response.getStatusCode().value());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("must not be null", body.getFields().get("productId"));
    }

    // ---------------------------------------------------------------
    // 6) Generic Exception
    // ---------------------------------------------------------------
    @Test
    void testHandleGenericException() {
        Exception ex = new RuntimeException("boom");

        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/prices/final");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, req);

        assertEquals(500, response.getStatusCode().value());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Error interno del servidor", body.getMessage());
    }

    // ---------------------------------------------------------------
    // helper
    // ---------------------------------------------------------------
    private static ConstraintViolation<Object> getViolationMocked() {
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("productId");

        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be null");

        return violation;
    }
}
