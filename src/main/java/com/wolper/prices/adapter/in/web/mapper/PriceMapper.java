package com.wolper.prices.adapter.in.web.mapper;

import com.wolper.prices.adapter.in.web.dto.PriceResponse;
import com.wolper.prices.domain.model.BrandPrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para convertir entre entidades de dominio y DTOs.
 * MapStruct genera la implementación en tiempo de compilación.
 */
@Mapper(componentModel = "spring")
public interface PriceMapper {
    
    @Mapping(source = "currency", target = "curr")
    PriceResponse toResponse(BrandPrice price);
}
