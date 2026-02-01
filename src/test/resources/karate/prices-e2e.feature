Feature: Price API End-to-End Tests
  
  Background:
    * url baseUrl
    * def basePath = '/prices/final'

  Scenario: Test 1 - Get price at 10:00 on June 14th for product 35455 brand 1
    Given path basePath
    And param date = '2020-06-14T10:00:00'
    And param productId = 35455
    And param brandId = 1
    When method GET
    Then status 200
    And match response.productId == 35455
    And match response.brandId == 1
    And match response.priceList == 1
    And match response.price == 35.50
    And match response.curr == 'EUR'
    And match response.startDate == '2020-06-14T00:00:00'
    And match response.endDate == '2020-12-31T23:59:59'
    And match responseHeaders['X-Request-ID'][0] != null
    And match responseHeaders['Cache-Control'][0] == 'no-cache'

  Scenario: Test 2 - Get price at 16:00 on June 14th for product 35455 brand 1
    Given path basePath
    And param date = '2020-06-14T16:00:00'
    And param productId = 35455
    And param brandId = 1
    When method GET
    Then status 200
    And match response.productId == 35455
    And match response.brandId == 1
    And match response.priceList == 2
    And match response.price == 25.45
    And match response.curr == 'EUR'
    And match response.startDate == '2020-06-14T15:00:00'
    And match response.endDate == '2020-06-14T18:30:00'

  Scenario: Test 3 - Get price at 21:00 on June 14th for product 35455 brand 1
    Given path basePath
    And param date = '2020-06-14T21:00:00'
    And param productId = 35455
    And param brandId = 1
    When method GET
    Then status 200
    And match response.productId == 35455
    And match response.brandId == 1
    And match response.priceList == 1
    And match response.price == 35.50
    And match response.curr == 'EUR'

  Scenario: Test 4 - Get price at 10:00 on June 15th for product 35455 brand 1
    Given path basePath
    And param date = '2020-06-15T10:00:00'
    And param productId = 35455
    And param brandId = 1
    When method GET
    Then status 200
    And match response.productId == 35455
    And match response.brandId == 1
    And match response.priceList == 3
    And match response.price == 30.50
    And match response.curr == 'EUR'
    And match response.startDate == '2020-06-15T00:00:00'
    And match response.endDate == '2020-06-15T11:00:00'

  Scenario: Test 5 - Get price at 21:00 on June 16th for product 35455 brand 1
    Given path basePath
    And param date = '2020-06-16T21:00:00'
    And param productId = 35455
    And param brandId = 1
    When method GET
    Then status 200
    And match response.productId == 35455
    And match response.brandId == 1
    And match response.priceList == 4
    And match response.price == 38.95
    And match response.curr == 'EUR'
    And match response.startDate == '2020-06-15T16:00:00'
    And match response.endDate == '2020-12-31T23:59:59'

  Scenario: Validation - Missing required parameter should return 400
    Given path basePath
    And param date = '2020-06-14T10:00:00'
    And param productId = 35455
    # Missing brandId
    When method GET
    Then status 400
    And match response.status == 400

  Scenario: Validation - Invalid productId should return 400
    Given path basePath
    And param date = '2020-06-14T10:00:00'
    And param productId = 0
    And param brandId = 1
    When method GET
    Then status 400
    And match response.status == 400
    And match response.message != null

  Scenario: Exception - Price not found should return 404
    Given path basePath
    And param date = '2025-01-01T10:00:00'
    And param productId = 35455
    And param brandId = 1
    When method GET
    Then status 404
    And match response.status == 404
    And match response.message contains 'No se encontró precio'
