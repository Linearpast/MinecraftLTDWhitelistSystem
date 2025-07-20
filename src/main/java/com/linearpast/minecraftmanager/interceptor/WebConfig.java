package com.linearpast.minecraftmanager.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminInterceptor adminInterceptor;
    @Autowired
    private PlayerInterceptor playerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns(
                        "/admin/**",
                        "/api/**"
                ).excludePathPatterns(
                        "/admin/login/**",
                        "/api/answer/**",
                        "/api/confirm",
                        "/api/region/findRegion"
                );
        registry.addInterceptor(playerInterceptor)
                .addPathPatterns(
                        "/player/**",
                        "/api/answer/**"
                ).excludePathPatterns(
                        "/player/login/**",
                        "/player/emailSuccess"
                );
    }
}