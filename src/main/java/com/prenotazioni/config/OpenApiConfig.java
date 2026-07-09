package com.prenotazioni.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI prenotazioniOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gestionale Prenotazioni API")
                        .description("Documentazione API del backend Gestionale Prenotazioni")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Umberto Fergola"))
                        .license(new License()
                                .name("Private Project")))
                .externalDocs(new ExternalDocumentation()
                        .description("Backend Java/Spring Boot per gestione prenotazioni"));
    }
}