package com.bos.interceptor;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


//@Configuration
public class MyInterceptor extends WebMvcConfigurationSupport {


    @Override //添加自定义拦截器到springboot拦截器池中
    protected void addInterceptors(InterceptorRegistry registry){
        System.out.println("添加拦截器");
        registry.addInterceptor(new JwtInterceptor()).addPathPatterns("/**").excludePathPatterns("/user/login");
        super.addInterceptors(registry);
    }
}