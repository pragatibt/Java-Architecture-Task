package com.example.medico.demo.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SanitizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        req.getParameterMap().forEach((key, values) -> {
            for (int i = 0; i < values.length; i++) {

                values[i] = values[i]
                        .replaceAll("<", "")
                        .replaceAll(">", "")
                        .replaceAll("script", "");
            }
        });

        chain.doFilter(request, response);
    }
}