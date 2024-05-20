package my.code.effectivemobiletest.controllers.util;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI configure() {
        return new OpenAPI()
                .info(new Info()
                        .title("Effective Mobile")
                        .contact(new Contact().name("k.daniil2023@gmail.com"))
                        .version("1.0.0")
                );
    }
}