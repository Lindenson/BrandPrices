-- Schema para la tabla PRICES

DROP TABLE IF EXISTS prices;

CREATE TABLE prices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand_id BIGINT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    price_list BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    priority INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    curr VARCHAR(3) NOT NULL,
    CONSTRAINT chk_dates CHECK (end_date >= start_date),
    CONSTRAINT chk_price CHECK (price >= 0),
    CONSTRAINT chk_priority CHECK (priority >= 0)
);

-- Índices para optimizar las consultas
CREATE INDEX idx_product_brand_date ON prices(product_id, brand_id, start_date, end_date);
CREATE INDEX idx_priority ON prices(priority DESC);
