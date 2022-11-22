package br.edu.ifpb.biblioteca.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("Biblioteca")
                .description("Documentação do projeto \"Biblioteca\"")
                .version("v1.0.0")
                .contact(new Contact()
                        .name("José Henrique")
                        .url("https://github.com/Joshaby")
                        .email("josehenriquebrito55@gmail.com"))
                .termsOfService("Nada")
                .license(new License()
                        .name("GNU GPL v2")
                        .url("https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html")));
    }
}
