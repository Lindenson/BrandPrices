# Guía de Instalación Rápida

## Inicio Rápido (5 minutos)

### Prerrequisitos
- Java 21+
- Maven 3.9+

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/Lindenson/BrandPrice.git
cd BrandPrice

# 2. Compilar y ejecutar tests
mvn clean verify

# 3. Ejecutar la aplicación
mvn spring-boot:run
```

### Verificar Instalación

```bash
# Hacer una petición de prueba
curl "http://localhost:8080/prices/final?date=2020-06-14T10:00:00&productId=35455&brandId=1"
```

**Respuesta esperada:**
```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 1,
  "startDate": "2020-06-14T00:00:00",
  "endDate": "2020-12-31T23:59:59",
  "price": 35.50,
  "curr": "EUR"
}
```

### Acceder a Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

### Acceder a H2 Console

```
http://localhost:8080/h2-console
```

---

## Con Docker

```bash
# Build y ejecutar
docker compose up --build

# Verificar
curl "http://localhost:8080/prices/final?date=2020-06-14T10:00:00&productId=35455&brandId=1"
```

---

## Ejecutar Tests

```bash
# Tests unitarios
mvn test

# Tests de integración
mvn verify

# Ver cobertura
mvn jacoco:report
# Abrir: target/site/jacoco/index.html
```

---

## Ejemplos de Uso

### Escenario 1: Precio base (10:00, día 14)
```bash
curl "http://localhost:8080/prices/final?date=2020-06-14T10:00:00&productId=35455&brandId=1"
# Respuesta: priceList=1, price=35.50
```

### Escenario 2: Precio promocional (16:00, día 14)
```bash
curl "http://localhost:8080/prices/final?date=2020-06-14T16:00:00&productId=35455&brandId=1"
# Respuesta: priceList=2, price=25.45
```

### Escenario 3: Después de promoción (21:00, día 14)
```bash
curl "http://localhost:8080/prices/final?date=2020-06-14T21:00:00&productId=35455&brandId=1"
# Respuesta: priceList=1, price=35.50
```

### Escenario 4: Mañana siguiente (10:00, día 15)
```bash
curl "http://localhost:8080/prices/final?date=2020-06-15T10:00:00&productId=35455&brandId=1"
# Respuesta: priceList=3, price=30.50
```

### Escenario 5: Noche siguiente (21:00, día 16)
```bash
curl "http://localhost:8080/prices/final?date=2020-06-16T21:00:00&productId=35455&brandId=1"
# Respuesta: priceList=4, price=38.95
```

---

## Solución de Problemas

### Error: Java version incorrecta
```bash
java -version  # Debe ser 21 o superior
```

### Error: Puerto 8080 ya en uso
```bash
# Cambiar puerto en application.yml
server:
  port: 8081
```

### Error: Tests fallan
```bash
# Limpiar y recompilar
mvn clean install -U
```

---

## Soporte

Si encuentras algún problema:
1. Revisa los [logs de la aplicación](logs/)
2. Consulta la [documentación completa](README.md)
3. Abre un [issue en GitHub](https://github.com/Lindenson/BrandPrice/issues)
