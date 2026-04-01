package com.example.board.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ClientIdInterceptor implements HandlerInterceptor {

    public static final String CLIENT_ID_HEADER = "client-id";
    public static final String CLIENT_ID_ATTRIBUTE = "clientId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String clientId = request.getHeader(CLIENT_ID_HEADER);
        if (clientId != null && !clientId.isEmpty()) {
            request.setAttribute(CLIENT_ID_ATTRIBUTE, clientId);
        }
        return true;
    }
}
