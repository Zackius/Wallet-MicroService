package com.example.wallet_app.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${base.url}")
    private String baseUrl;

    @Bean
    public OpenAPI customOpenAPI() {

        List<Server> servers = new ArrayList<>();
        if (activeProfile.equals("local")) {
            servers.add(new Server().url("http://localhost:8065"));

        } else {
            servers.add(new Server().url(baseUrl));
        }


        return new OpenAPI()
                .info(new Info()
                        .title("Wallet App")
                        .version("1.0")
                        .description("Wallet and settlement App")
                        .contact(new Contact().name("Wallet").email("ndunguzachary24@gmail.com")
                        )).servers(servers);
    }
}

