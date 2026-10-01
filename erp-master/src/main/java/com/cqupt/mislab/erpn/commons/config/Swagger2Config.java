package com.cqupt.mislab.erpn.commons.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @program: com.cqupt.mislab.erpn.config
 * @description:
 * @author: keji0
 * @date: 2020/7/20 9:40
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {
//
//    @Value("${swagger2.enable}")
//    private boolean enable;

    @Bean
    public Docket algorithmApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(createApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.cqupt.mislab.erpn.controller"))
                .paths(PathSelectors.any())
                .build()
//                .enable(enable)
                .useDefaultResponseMessages(false);
    }

    private ApiInfo createApiInfo() {
        return new ApiInfoBuilder().title("erp接口文档")
                .contact(new Contact("spx0315", "spx0315.github.io", "1445024781@qq.com"))
                .version("1.0")
                .build();
    }
}
