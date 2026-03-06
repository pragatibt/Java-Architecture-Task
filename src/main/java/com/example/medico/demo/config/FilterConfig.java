
package com.example.medico.demo.config;

import com.example.medico.demo.filter.SanitizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<SanitizationFilter> sanitizationFilter() {

        FilterRegistrationBean<SanitizationFilter> registration =
                new FilterRegistrationBean<>();

        registration.setFilter(new SanitizationFilter());
        registration.addUrlPatterns("/*");

        return registration;
    }
}