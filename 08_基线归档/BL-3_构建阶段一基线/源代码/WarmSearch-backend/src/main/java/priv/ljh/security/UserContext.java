package priv.ljh.security;

/**
 * 基于 ThreadLocal 的当前登录用户上下文
 */
public class UserContext {

    private static final ThreadLocal<LoginUser> CONTEXT = new ThreadLocal<>();

    public static void set(LoginUser user) {
        CONTEXT.set(user);
    }

    public static LoginUser get() {
        return CONTEXT.get();
    }

    public static Long getCurrentId() {
        LoginUser user = CONTEXT.get();
        return user == null ? null : user.getId();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
