package com.example.order_system.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/admin-dish.html")
                .addPathPatterns("/admin-order.html")
                .addPathPatterns("/dish/add")
                .addPathPatterns("/dish/delete")
                .addPathPatterns("/order/changeState");

        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/order/add")
                .addPathPatterns("/dish/getDish");
    }
}
