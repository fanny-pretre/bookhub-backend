package fr.eni.bookhubbackend.security;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI bookHubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BookHub API")
                        .description("Documentation API REST du projet BookHub")
                        .version("1.0"));
    }

}
