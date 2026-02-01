package com.wolper.prices.e2e;

import com.intuit.karate.junit5.Karate;

/**
 * Runner para ejecutar tests E2E con Karate.
 */
class PricesE2E {
    
    @Karate.Test
    Karate testPricesE2E() {
        return Karate.run("classpath:karate/prices-e2e.feature")
                .systemProperty("karate.env", "dev");
    }
}
