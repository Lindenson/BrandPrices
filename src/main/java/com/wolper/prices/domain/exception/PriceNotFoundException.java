package com.wolper.prices.domain.exception;

/**
 * Excepción lanzada cuando no se encuentra un precio aplicable
 * para los criterios especificados.
 */
public class PriceNotFoundException extends RuntimeException {
    
    private final Long productId;
    private final Long brandId;
    private final String applicationDate;
    
    public PriceNotFoundException(Long productId, Long brandId, String applicationDate) {
        super(String.format("No se encontró precio para productId=%d, brandId=%d, fecha=%s",
                productId, brandId, applicationDate));
        this.productId = productId;
        this.brandId = brandId;
        this.applicationDate = applicationDate;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public Long getBrandId() {
        return brandId;
    }
    
    public String getApplicationDate() {
        return applicationDate;
    }
}
