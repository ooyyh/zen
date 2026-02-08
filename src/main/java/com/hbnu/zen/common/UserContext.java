package com.hbnu.zen.common;

public final class UserContext {
    private static final ThreadLocal<UserSession> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(UserSession session) {
        HOLDER.set(session);
    }

    public static UserSession get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
