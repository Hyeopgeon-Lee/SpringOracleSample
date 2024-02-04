package kopo.poly.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private Info apiInfo() {
        return new Info()
                .title("Rest API App")
                .description("Rest API App Description!!")
                .contact(new Contact().name("Prof. Hyeopgeon Lee")
                        .email("hglee67@kopo.ac.kr")
                        .url("https://www.kopo.ac.kr/kangseo/content.do?menu=1547"))
                .license(new License()
                        .name("이협건 교수에게 교육받은 분들은 모두 자유롭게 사용 가능"))
                .version("1.0.0");
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().components(new Components()).info(apiInfo());
    }
}
