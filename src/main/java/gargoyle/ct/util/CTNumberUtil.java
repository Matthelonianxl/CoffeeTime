package gargoyle.ct.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
public final class CTNumberUtil {
    private static final String METHOD_VALUE_OF = "valueOf";
    private static final String MSG_CANNOT_CREATE_INSTANCE_OF_0 = "Cannot create instance of {0}";

    private CTNumberUtil() {
    }

    @NotNull
    @SuppressWarnings({"unchecked", "ChainOfInstanceofChecks"})
    public static <T extends Number> T fromInt(@NotNull Class<T> type, int value) {
        try {
            if (type == Integer.class) {
                return (T) Integer.valueOf(value);
            }
            if (type == Long.class) {
                return (T) Long.valueOf(value);
            }
            if (type == Double.class) {
                return (T) Double.valueOf(value);
            }
            if (type == Float.class) {
                return (T) Float.valueOf(value);
            }
            if (type == Byte.class) {
                return (T) Byte.valueOf((byte) value);
            }
            return (T) type.getMethod(METHOD_VALUE_OF, String.class).invoke(null, String.valueOf(value));
        } catch (@NotNull IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @SuppressWarnings({"unchecked", "ChainOfInstanceofChecks"})
    public static <T> T getDefault(@NotNull Class<T> type) {
        if (type == Integer.class) {
            return (T) Integer.valueOf(0);
        }
        if (type == Long.class) {
            return (T) Long.valueOf(0);
        }
        if (type == Double.class) {
            return (T) Double.valueOf(0);
        }
        if (type == Float.class) {
            return (T) Float.valueOf(0);
        }
        if (type == Byte.class) {
            return (T) Byte.valueOf((byte) 0);
        }
        T def;
        try {
            def = Number.class.isAssignableFrom(type) ?
                    (T) type.getMethod(METHOD_VALUE_OF, String.class).invoke(null, "0") :
                    type.getConstructor().newInstance();
        } catch (@NotNull IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException
                ex) {
            throw new IllegalArgumentException(MessageFormat.format(MSG_CANNOT_CREATE_INSTANCE_OF_0, type), ex);
        }
        return def;
    }

    public static Integer getInteger(Object value) {
        return Integer.valueOf(String.valueOf(value));
    }

    public static int toRange(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static Integer toRange(Integer value, Integer min, Integer max) {
        return Math.max(min, Math.min(max, value));
    }

    public static <T extends Number> int toRange(T min, T max, T value) {
        return Math.max(toInt(min), Math.min(toInt(max), toInt(value)));
    }

    public static <T extends Number> int toInt(T value) {
        return Integer.parseInt(String.valueOf(value));
    }
}
