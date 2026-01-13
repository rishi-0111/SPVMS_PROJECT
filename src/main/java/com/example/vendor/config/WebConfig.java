
package com.example.vendor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import com.example.vendor.interceptor.AuditInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuditInterceptor interceptor;

    public WebConfig(AuditInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/health");
    }
}
