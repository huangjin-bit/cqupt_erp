package com.cqupt.mislab.erpn.commons.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @program: work-Mis.Lab
 * @description: 拦截器
 * @author: 宋丽
 * @create: 2019-11-22 20:58
 **/
@Configuration
public class LoginConfig implements WebMvcConfigurer {

    @Resource
    private LoginInterceptor loginInterceptor;

    /**
     * 拦截器配置
     * @param registry 注册类
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/regester/addregester")
                .excludePathPatterns("/user/VerifyCode")
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**", "/doc.html/**")
                .excludePathPatterns("/teacher/login")
                .excludePathPatterns("/user/getBasic")
                .excludePathPatterns("/personal/report/**")
                .excludePathPatterns("/help/**")
                .excludePathPatterns("/download/**")
                .excludePathPatterns("/superadmin/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowedOrigins("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/personal/report/**").addResourceLocations("file:C:\\erpnReport\\document\\");
        registry.addResourceHandler("/help/**").addResourceLocations("file:C:\\erpnReport\\help\\");
    }

}

