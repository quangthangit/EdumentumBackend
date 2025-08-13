package com.EdumentumBackend.EdumentumBackend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiTimingInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long start = (Long) request.getAttribute(START_TIME);
        long duration = System.currentTimeMillis() - start;

        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();

        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("API_LOGGER");
        logger.info(
                "{{\"method\":\"{}\",\"uri\":\"{}\",\"status\":{},\"duration_ms\":{}}}",
                method, uri, status, duration
        );
    }
}
