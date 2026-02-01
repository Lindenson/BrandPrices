package com.wolper.prices.adapter.out.persistence;

import com.wolper.prices.application.port.out.PriceRepository;
import com.wolper.prices.domain.model.Price;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Adaptador de persistencia que implementa el puerto de salida
 * utilizando JDBC de bajo nivel para máximo control y performance.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcPriceRepository implements PriceRepository {
    
    private final NamedParameterJdbcTemplate jdbcTemplate;
    
    private static final String FIND_APPLICABLE_PRICES_QUERY = """
        SELECT 
            id,
            brand_id,
            start_date,
            end_date,
            price_list,
            product_id,
            priority,
            price,
            curr
        FROM prices
        WHERE product_id = :productId
          AND brand_id = :brandId
          AND :applicationDate BETWEEN start_date AND end_date
        ORDER BY priority DESC, price_list DESC
        """;
    
    @Override
    public List<Price> findApplicablePrices(LocalDateTime applicationDate, Long productId, Long brandId) {
        log.debug("Ejecutando consulta: productId={}, brandId={}, fecha={}", 
                  productId, brandId, applicationDate);
        
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("productId", productId)
                .addValue("brandId", brandId)
                .addValue("applicationDate", applicationDate);
        
        List<Price> prices = jdbcTemplate.query(FIND_APPLICABLE_PRICES_QUERY, params, new PriceRowMapper());
        
        log.debug("Encontrados {} precios aplicables", prices.size());
        
        return prices;
    }
    
    /**
     * RowMapper para convertir ResultSet a entidad Price.
     */
    private static class PriceRowMapper implements RowMapper<Price> {
        @Override
        public Price mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Price.builder()
                    .id(rs.getLong("id"))
                    .brandId(rs.getLong("brand_id"))
                    .startDate(rs.getTimestamp("start_date").toLocalDateTime())
                    .endDate(rs.getTimestamp("end_date").toLocalDateTime())
                    .priceList(rs.getLong("price_list"))
                    .productId(rs.getLong("product_id"))
                    .priority(rs.getInt("priority"))
                    .price(rs.getBigDecimal("price"))
                    .currency(rs.getString("curr"))
                    .build();
        }
    }
}
