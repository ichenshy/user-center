package com.chen.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * Swagger2配置信息
 * @author gala
 */
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {
    //@Bean(value = "defaultApi2")
    @Bean
    public Docket webApiConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(webApiInfo())
                .select()
                //只显示api路径下的页面
                .apis(RequestHandlerSelectors.basePackage("com.chen.user.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**api信息
     * @param
     * @return
     */

    private ApiInfo webApiInfo() {
        return new ApiInfoBuilder()
                .title("用户中心")
                .description("本文档描述了用户中心接口定义")
                .termsOfServiceUrl("https://github.com/galaxylq")
                .contact(new Contact("baidu", "http://baidu.com", "123456789@qq.com"))
                .version("1.0")
                .build();
    }
}
