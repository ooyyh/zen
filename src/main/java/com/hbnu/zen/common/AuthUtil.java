package com.hbnu.zen.common;

public final class AuthUtil {
    private AuthUtil() {
    }

    public static UserSession getSession() {
        UserSession session = UserContext.get();
        if (session == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录");
        }
        return session;
    }

    public static Long getUserId() {
        return getSession().getId();
    }

    public static String getRole() {
        return getSession().getRole();
    }

    public static void requireRole(String role) {
        UserSession session = getSession();
        if (!role.equals(session.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限访问");
        }
    }
}
