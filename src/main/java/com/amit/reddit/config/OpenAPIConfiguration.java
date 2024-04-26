package com.amit.reddit.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {


    @Bean
    public OpenAPI expenseAPI() {
        return new OpenAPI()
                .info(new Info().title("Reddit API Documentation")
                        .description("API for Reddit Application")
                        .version("v0.0.0")
                        .license(new License().name("Reddit License Version 1.0").url("http://redditApiDocumention.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Expense Tracker Wiki Documentation")
                        .url("https://expensetracker.wiki/docs"));
    }
}