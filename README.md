# Brand Price Service

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-green.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Servicio REST desarrollado con **Spring Boot** que permite consultar el precio final de un producto en una cadena de tiendas para una fecha específica, aplicando reglas de prioridad y tarifas.

---

## Tabla de Contenidos

- [Características](#características)
- [Arquitectura](#arquitectura)
- [Requisitos Previos](#requisitos-previos)
- [Instalación y Ejecución](#instalación-y-ejecución)
- [Uso de la API](#uso-de-la-api)
- [Testing](#testing)
- [Docker](#docker)
- [Documentación API](#documentación-api)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Decisiones Técnicas](#decisiones-técnicas)

---

## Características

- ✅ **Arquitectura Hexagonal**: Separación clara entre dominio, aplicación y adaptadores
- ✅ **API First**: Diseño basado en especificación OpenAPI 3.0
- ✅ **Validación Robusta**: Validación de parámetros con mensajes de error descriptivos
- ✅ **Gestión de Excepciones**: Manejo centralizado de errores con respuestas consistentes
- ✅ **Tests Completos**: Tests unitarios, de integración y E2E con Karate
- ✅ **Cobertura >80%**: Verificada con JaCoCo
- ✅ **Swagger UI**: Documentación interactiva de la API
- ✅ **Docker**: Containerización lista para producción
- ✅ **Base de Datos H2**: En memoria para desarrollo y testing
- ✅ **Logging Estructurado**: Trazabilidad con request IDs únicos

---

## Arquitectura

El proyecto sigue una **arquitectura hexagonal** (puertos y adaptadores):

```
┌─────────────────────────────────────────────────────────┐
│                  Capa de Presentación                   │
│  ┌────────────────────────────────────────────────────┐ │
│  │   PriceController (Adaptador In - REST)            │ │
│  │   - Validación de entrada                          │ │
│  │   - Mapeo DTO ↔ Dominio                            │ │
│  │   - Manejo de excepciones                          │ │
│  └────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│                  Capa de Aplicación                     │
│  ┌────────────────────────────────────────────────────┐ │
│  │   GetFinalPriceService                             │ │
│  │   - Lógica de negocio                              │ │
│  │   - Orquestación                                   │ │
│  └────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│                    Capa de Dominio                      │
│  ┌────────────────────────────────────────────────────┐ │
│  │   Price (Entidad)                                  │ │
│  │   - Reglas de negocio                              │ │
│  │   - Validaciones de dominio                        │ │
│  └────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│                 Capa de Persistencia                    │
│  ┌────────────────────────────────────────────────────┐ │
│  │   JdbcPriceRepository (Adaptador Out)              │ │
│  │   - Acceso a datos con JDBC                        │ │
│  │   - Optimización de queries                        │ │
│  └────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

### Flujo de Datos

1. **Request** → Controlador valida parámetros
2. **Controlador** → Invoca caso de uso
3. **Caso de Uso** → Consulta repositorio
4. **Repositorio** → Ejecuta query optimizada en BD
5. **Caso de Uso** → Aplica lógica de prioridad
6. **Controlador** → Mapea a DTO y retorna response

---

## Requisitos Previos

- **Java 21** o superior
- **Maven 3.9+**
- **Docker**
- **Docker Composer**


---

## Instalación y Ejecución

### Opción 1: Maven

```bash
# Clonar el repositorio
git clone https://github.com/Lindenson/BrandPrices.git
cd BrandPrice

# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

### Opción 2: JAR Ejecutable

```bash
# Compilar
mvn clean package

# Ejecutar
java -jar target/brand-price-1.0.0.jar
```

### Opción 3: Docker (Recomendado)

```bash
# Build y ejecución con docker-compose
docker-compose up --build

# O usando Docker directamente
docker build -t brand-price-service .
docker run -p 8080:8080 brand-price-service
```

---

## Uso de la API

### Endpoint Principal

**GET** `/prices/final`

Retorna el precio aplicable para un producto en una marca específica en una fecha determinada.

#### Parámetros

| Parámetro   | Tipo     | Requerido | Descripción                                  | Ejemplo                |
|-------------|----------|-----------|----------------------------------------------|------------------------|
| `date`      | DateTime | Sí        | Fecha de aplicación (ISO 8601)               | `2020-06-14T10:00:00` |
| `productId` | Long     | Sí        | Identificador del producto (>0)              | `35455`               |
| `brandId`   | Long     | Sí        | Identificador de la marca (>0)               | `1`                   |

#### Ejemplo de Petición

```bash
curl "http://localhost:8080/prices/final?date=2020-06-14T10:00:00&productId=35455&brandId=1"
```

#### Ejemplo de Respuesta Exitosa (200 OK)

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

#### Respuestas de Error

**400 Bad Request** - Parámetros inválidos

```json
{
  "timestamp": "2026-01-31T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación",
  "path": "/prices/final",
  "fields": {
    "productId": "El productId debe ser mayor que 0"
  }
}
```

**404 Not Found** - Precio no encontrado

```json
{
  "timestamp": "2026-01-31T10:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "No se encontró precio para productId=35455, brandId=1, fecha=2025-01-01T10:00:00",
  "path": "/prices/final"
}
```

---

## Testing

El proyecto incluye 3 niveles de testing:

### Tests Unitarios

```bash
# Ejecutar solo tests unitarios
mvn test
```

Cobertura: **>90%** en capa de dominio y aplicación

### Tests de Integración

```bash
# Ejecutar tests de integración con MockMvc
mvn verify
```

Valida los **5 escenarios** solicitados:

1. ✅ **14-06-2020 10:00** → `priceList=1`, `price=35.50 EUR`
2. ✅ **14-06-2020 16:00** → `priceList=2`, `price=25.45 EUR`
3. ✅ **14-06-2020 21:00** → `priceList=1`, `price=35.50 EUR`
4. ✅ **15-06-2020 10:00** → `priceList=3`, `price=30.50 EUR`
5. ✅ **16-06-2020 21:00** → `priceList=4`, `price=38.95 EUR`

### Tests E2E con Karate

```bash
# Ejecutar todos los tests incluyendo E2E
mvn verify -Pkarate
```

Los tests E2E validan:
- ✅ Todos los escenarios funcionales
- ✅ Validación de parámetros
- ✅ Gestión de excepciones
- ✅ Headers de respuesta

### Reporte de Cobertura

```bash
# Generar reporte JaCoCo
mvn clean test jacoco:report

# Ver reporte en:
# target/site/jacoco/index.html
```

---

## Docker

### Build de la Imagen

```bash
docker build -t brand-price-service:latest .
```

### Ejecución

```bash
# Con docker-compose (recomendado)
docker-compose up -d

# Con docker run
docker run -d -p 8080:8080 --name brand-price brand-price-service:latest
```

### Health Check

```bash
# Verificar salud del contenedor
docker ps
# HEALTHCHECK debe mostrar "healthy"

# O hacer petición directa
curl http://localhost:8080/prices/final?date=2020-06-14T10:00:00&productId=35455&brandId=1
```

### Logs

```bash
# Ver logs
docker-compose logs -f brand-price-service
```

---

## Documentación API

### Swagger UI

Acceder a la documentación interactiva:

```
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI Spec

Especificación en formato JSON:

```
http://localhost:8080/api-docs
```

### H2 Console

Acceder a la consola de base de datos:

```
http://localhost:8080/h2-console
```

**Credenciales:**
- JDBC URL: `jdbc:h2:mem:pricesdb`
- Username: `sa`
- Password: *(vacío)*

---

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/wolper/prices/
│   │   ├── adapter/
│   │   │   ├── in/
│   │   │   │   └── web/
│   │   │   │       ├── PriceController.java
│   │   │   │       ├── dto/
│   │   │   │       ├── mapper/
│   │   │   │       └── exception/
│   │   │   └── out/
│   │   │       └── persistence/
│   │   │           └── JdbcPriceRepository.java
│   │   ├── application/
│   │   │   ├── port/
│   │   │   │   ├── in/
│   │   │   │   │   └── GetFinalPriceUseCase.java
│   │   │   │   └── out/
│   │   │   │       └── PriceRepository.java
│   │   │   └── service/
│   │   │       └── GetFinalPriceService.java
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   └── Price.java
│   │   │   └── exception/
│   │   │       └── PriceNotFoundException.java
│   │   ├── config/
│   │   │   └── OpenApiConfig.java
│   │   └── PricesApplication.java
│   └── resources/
│       ├── application.yml
│       ├── schema.sql
│       ├── data.sql
│       └── openapi.yaml
└── test/
 
```

---

## Decisiones Técnicas

### ¿Por qué Arquitectura Hexagonal?

- **Separación de responsabilidades**: Facilita testing y mantenimiento
- **Independencia del framework**: El dominio no depende de Spring
- **Flexibilidad**: Fácil cambio de adaptadores (ej: cambiar JDBC por JPA)

### ¿Por qué JDBC en lugar de JPA/Hibernate?

- **Performance**: Control total sobre las queries SQL
- **Simplicidad**: Menos overhead para operaciones simples
- **Optimización**: Query específica con ORDER BY para evitar ordenamiento en memoria

### ¿Por qué MapStruct?

- **Performance**: Generación de código en tiempo de compilación
- **Type-safe**: Errores de mapeo detectados en compilación
- **Mantenibilidad**: Código generado automáticamente

### ¿Por qué Karate para E2E?

- **Simplicidad**: Sintaxis declarativa tipo BDD
- **Completitud**: Validación de headers, status codes, JSON paths
- **Integración**: Se ejecuta con JUnit 5, compatible con Maven

### ¿Por qué H2?

- **Velocidad**: Tests extremadamente rápidos
- **Portabilidad**: No requiere instalación de BD externa
- **Consistencia**: Mismo esquema en todos los entornos

---

## Datos de Ejemplo

La aplicación se inicializa con los siguientes precios:

| BRAND_ID | START_DATE          | END_DATE            | PRICE_LIST | PRODUCT_ID | PRIORITY | PRICE | CURR |
|----------|---------------------|---------------------|------------|------------|----------|-------|------|
| 1        | 2020-06-14 00:00:00 | 2020-12-31 23:59:59 | 1          | 35455      | 0        | 35.50 | EUR  |
| 1        | 2020-06-14 15:00:00 | 2020-06-14 18:30:00 | 2          | 35455      | 1        | 25.45 | EUR  |
| 1        | 2020-06-15 00:00:00 | 2020-06-15 11:00:00 | 3          | 35455      | 1        | 30.50 | EUR  |
| 1        | 2020-06-15 16:00:00 | 2020-12-31 23:59:59 | 4          | 35455      | 1        | 38.95 | EUR  |

**Nota:** BRAND_ID 1 = ZARA

---

## Monitorización

### Request Tracking

Cada petición incluye un header `X-Request-ID` único para trazabilidad:

```bash
curl -i http://localhost:8080/prices/final?date=2020-06-14T10:00:00&productId=35455&brandId=1

# Response headers incluyen:
# X-Request-ID: 550e8400-e29b-41d4-a716-446655440000
# Cache-Control: no-cache
```

### Logs Estructurados

```
2026-01-31 10:00:00 [http-nio-8080-exec-1] INFO  c.w.p.a.i.w.PriceController - [550e8400...] GET /prices/final - date=2020-06-14T10:00:00, productId=35455, brandId=1
2026-01-31 10:00:00 [http-nio-8080-exec-1] DEBUG c.w.p.a.s.GetFinalPriceService - Buscando precio para productId=35455, brandId=1, fecha=2020-06-14T10:00:00
2026-01-31 10:00:00 [http-nio-8080-exec-1] INFO  c.w.p.a.s.GetFinalPriceService - Precio encontrado: priceList=1, price=35.50 EUR
```

---

## Contribución

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

---

## Autor

**Lindenson** - [GitHub](https://github.com/Lindenson)

---

## Agradecimientos

- Spring Boot Team
- Karate DSL
- MapStruct
- Comunidad Open Source

---

