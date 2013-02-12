package tr.rimerun.jm;

import java.lang.reflect.Method;

public class ClassUtils {
    public static Method findMethod(Class<?> klass, String name, Class<?>... parameterTypes) {
        while (klass != null) {
            try {
                return klass.getDeclaredMethod(name, parameterTypes);
            } catch (NoSuchMethodException e) {
                klass = klass.getSuperclass();
            }
        }

        return null;
    }
}
