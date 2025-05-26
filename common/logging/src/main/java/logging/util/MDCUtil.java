package logging.util;

import org.slf4j.MDC;

public class MDCUtil {
    public static void setUser(String userId) {
        MDC.put("userId", userId);
    }

    public static void clear() {
        MDC.clear();
    }
}
