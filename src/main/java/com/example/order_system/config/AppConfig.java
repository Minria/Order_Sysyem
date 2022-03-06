package com.example.order_system.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {


        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/dish/add")
                .addPathPatterns("/dish/delete")
                .addPathPatterns("/order/changeState")
                .addPathPatterns("/admin-order.html")
                .addPathPatterns("/admin-dish.html");
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/order/add")
                .addPathPatterns("/order/findOrders")
                .addPathPatterns("user-dish.html")
                .addPathPatterns("user-order,html");
    }
}
