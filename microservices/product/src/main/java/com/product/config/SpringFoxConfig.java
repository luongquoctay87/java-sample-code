package com.product.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class SpringFoxConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("product-service") // /v3/api-docs/account-service
                .packagesToScan("com.product.controller")
                .build();
    }

    @Bean
    public OpenAPI openAPI(
            @Value("${openapi.service.title: Product Service}") String title,
            @Value("${openapi.service.version: 1.0}") String version,
            @Value("${openapi.service.url: http://localhost:7751/}") String url) {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                //.servers(List.of(new Server().url(url)))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        securitySchemeName,
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")))
                .security(List.of(new SecurityRequirement().addList(securitySchemeName)))
                .info(new Info().title(title)
                        .description("API document for products")
                        .version(version)
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

}
