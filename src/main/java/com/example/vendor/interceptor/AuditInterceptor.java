
package com.example.vendor.interceptor;

import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.example.vendor.service.AuditService;

@Component
public class AuditInterceptor implements HandlerInterceptor {

    private final AuditService service;

    public AuditInterceptor(AuditService service) {
        this.service = service;
    }

    @Override
    public boolean preHandle(HttpServletRequest r, HttpServletResponse s, Object h) {
        String user = r.getHeader("userId");
        if (user == null) user = "ANONYMOUS";
        service.log(user, r.getMethod(), r.getRequestURI());
        return true;
    }
}
