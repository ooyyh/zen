package com.hbnu.zen.security;

import com.hbnu.zen.common.ApiResponse;
import com.hbnu.zen.common.ErrorCode;
import com.hbnu.zen.common.UserContext;
import com.hbnu.zen.common.UserSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private static final Set<String> EXCLUDES = new HashSet<>(Arrays.asList(
            "/api/auth/login"
    ));

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if (EXCLUDES.contains(path)) {
            return true;
        }
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            writeUnauthorized(response, "未登录或登录已过期");
            return false;
        }
        String token = auth.substring("Bearer ".length());
        try {
            Claims claims = jwtUtil.parseToken(token);
            Long userId = claims.get("uid", Long.class);
            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);
            UserContext.set(new UserSession(userId, username, role));
            return true;
        } catch (Exception ex) {
            writeUnauthorized(response, "登录已过期");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        ApiResponse<Void> body = ApiResponse.error(ErrorCode.UNAUTHORIZED, message);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
