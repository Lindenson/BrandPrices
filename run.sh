#!/bin/bash

# Brand Price Service - Utility Script
# Facilita operaciones comunes del proyecto

set -e
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

build() {
    print_info "Compilando el proyecto..."
    mvn clean package -DskipTests
    print_success "Compilación completada"
}

test() {
    print_info "Ejecutando tests unitarios..."
    mvn test
    print_success "Tests completados"
}

integration_test() {
    print_info "Ejecutando tests de integración..."
    mvn verify
    print_success "Tests de integración completados"
}

run() {
    print_info "Iniciando Brand Price Service..."
    mvn spring-boot:run
}

docker_build() {
    print_info "Construyendo imagen Docker..."
    docker build -t brand-price-service:latest .
    print_success "Imagen Docker creada"
}

docker_run() {
    print_info "Ejecutando con Docker Compose..."
    docker compose up -d
    print_success "Servicio iniciado en http://localhost:8080"
    print_info "Ver logs: docker compose logs -f"
}

docker_stop() {
    print_info "Deteniendo contenedores..."
    docker compose down
    print_success "Contenedores detenidos"
}

logs() {
    docker compose logs -f brand-price-service
}

test_endpoints() {
    print_info "Probando endpoints..."
    
    BASE_URL="http://localhost:8080"
    
    echo ""
    print_info "Test 1: 2020-06-14 10:00"
    curl -s "$BASE_URL/prices/final?date=2020-06-14T10:00:00&productId=35455&brandId=1" | jq '.'
    
    echo ""
    print_info "Test 2: 2020-06-14 16:00"
    curl -s "$BASE_URL/prices/final?date=2020-06-14T16:00:00&productId=35455&brandId=1" | jq '.'
    
    echo ""
    print_info "Test 3: 2020-06-14 21:00"
    curl -s "$BASE_URL/prices/final?date=2020-06-14T21:00:00&productId=35455&brandId=1" | jq '.'
    
    echo ""
    print_info "Test 4: 2020-06-15 10:00"
    curl -s "$BASE_URL/prices/final?date=2020-06-15T10:00:00&productId=35455&brandId=1" | jq '.'
    
    echo ""
    print_info "Test 5: 2020-06-16 21:00"
    curl -s "$BASE_URL/prices/final?date=2020-06-16T21:00:00&productId=35455&brandId=1" | jq '.'
    
    echo ""
    print_success "Pruebas de endpoints completadas"
}

clean() {
    print_info "Limpiando proyecto..."
    mvn clean
    docker compose down -v 2>/dev/null || true
    print_success "Limpieza completada"
}

show_help() {
    cat << EOF
Brand Price Service - Utility Script

Uso: ./run.sh [comando]

Comandos disponibles:
  build              Compila el proyecto
  test               Ejecuta tests unitarios
  integration-test   Ejecuta tests de integración
  run                Ejecuta la aplicación con Maven
  docker-build       Construye la imagen Docker
  docker-run         Ejecuta con Docker Compose
  docker-stop        Detiene los contenedores Docker
  logs               Muestra los logs del contenedor
  test-endpoints     Prueba los endpoints de la API
  clean              Limpia el proyecto y contenedores
  help               Muestra esta ayuda

Ejemplos:
  ./run.sh build
  ./run.sh test
  ./run.sh docker-run
  ./run.sh test-endpoints

EOF
}

# Main
case "${1:-help}" in
    build)
        build
        ;;
    test)
        test
        ;;
    integration-test|it)
        integration_test
        ;;
    run)
        run
        ;;
    docker-build|db)
        docker_build
        ;;
    docker-run|dr)
        docker_run
        ;;
    docker-stop|ds)
        docker_stop
        ;;
    logs)
        logs
        ;;
    coverage|cov)
        coverage
        ;;
    test-endpoints|te)
        test_endpoints
        ;;
    clean)
        clean
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        print_error "Comando desconocido: $1"
        echo ""
        show_help
        exit 1
        ;;
esac
