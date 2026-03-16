package com.medico.medico.audit;

import com.medico.medico.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLoggingAspect {

    private final AuditService auditService;
    private final HttpServletRequest request;

    @Around("execution(* com.medico.medico.service..*(..)) && @annotation(org.springframework.security.access.prepost.PreAuthorize)")
    public Object logSecureMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String action = signature.getMethod().getName();
        String details = String.format("%s %s from %s", action, joinPoint.getArgs().length > 0 ? "with args" : "",
                request.getRemoteAddr());

        try {
            Object result = joinPoint.proceed();
            auditService.recordAction(username, action, details);
            return result;
        } catch (Throwable t) {
            auditService.recordAction(username, action + "_FAILED", details + " - " + t.getMessage());
            throw t;
        }
    }
}
