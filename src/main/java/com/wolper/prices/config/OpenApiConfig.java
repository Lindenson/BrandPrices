package com.wolper.prices.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API.
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:" + serverPort);
        server.setDescription("Development server");
        
        Contact contact = new Contact();
        contact.setName("API Support");
        contact.setEmail("support@example.com");
        
        Info info = new Info()
                .title("Brand Price API")
                .version("1.0.0")
                .description("API REST para consultar precios finales de productos por marca y fecha")
                .contact(contact);
        
        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}
