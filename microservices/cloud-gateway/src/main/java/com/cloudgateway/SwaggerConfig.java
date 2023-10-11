package com.cloudgateway;

import org.springdoc.core.SwaggerUiConfigParameters;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev"})
public class SwaggerConfig {

    @Bean
    public CommandLineRunner openApiGroups(
            RouteDefinitionLocator locator,
            SwaggerUiConfigParameters swaggerUiParameters) {
        return args -> {
            swaggerUiParameters.addGroup("account-service");
            swaggerUiParameters.addGroup("authentication-service");
        };
    }
}
