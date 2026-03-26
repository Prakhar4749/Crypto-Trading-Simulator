package com.prakhar.marketai.aspect;

import com.prakhar.common.util.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Value("${spring.application.name:service}")
    private String serviceName;

    @Around("execution(* com.prakhar..controller..*(..))")
    public Object logController(
        ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();
        String method = joinPoint.getSignature()
          .getName();
        String path = getRequestPath();
        String userId = getUserId();

        log.info(LogUtil.info(
          serviceName, path, userId,
          "→ " + method + " called"
        ));

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() 
              - start;

            // Only log slow requests (>1000ms)
            if (elapsed > 1000) {
                log.warn(LogUtil.slow(
                  serviceName, path, userId, elapsed
                ));
            }

            log.info(LogUtil.info(
              serviceName, path, userId,
              "← " + method + " completed"
            ));

            return result;

        } catch (Exception ex) {
            long elapsed = System.currentTimeMillis() 
              - start;
            log.error(LogUtil.error(
              serviceName, path, userId,
              ex.getMessage()
            ), ex);
            throw ex;
        }
    }

    private String getRequestPath() {
        try {
            ServletRequestAttributes attrs =
                (ServletRequestAttributes)
                RequestContextHolder
                  .getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest req = 
                  attrs.getRequest();
                return req.getMethod() + " " 
                  + req.getRequestURI();
            }
        } catch (Exception ignored) {}
        return "unknown";
    }

    private String getUserId() {
        try {
            ServletRequestAttributes attrs =
                (ServletRequestAttributes)
                RequestContextHolder
                  .getRequestAttributes();
            if (attrs != null) {
                return attrs.getRequest()
                  .getHeader("X-User-Id");
            }
        } catch (Exception ignored) {}
        return null;
    }
}
