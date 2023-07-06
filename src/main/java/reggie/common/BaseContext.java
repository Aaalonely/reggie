package reggie.common;

public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static void setThreadLocal(Long id) {
        threadLocal.set(id);
    }
    public static Long getThreadLocal() {
        return threadLocal.get();
    }
}
