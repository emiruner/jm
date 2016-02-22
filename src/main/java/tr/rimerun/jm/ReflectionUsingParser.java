package tr.rimerun.jm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReflectionUsingParser extends Parser {
    public ReflectionUsingParser(LinkedInputStream input) {
        super(input);
    }

    protected Object eval(String ruleName) {
        final Method method = ClassUtils.findMethod(getClass(), ruleName);

        if (method == null) {
            throw new RuntimeException("unknown rule: " + ruleName);
        }

        method.setAccessible(true);

        try {
            return method.invoke(this);
        } catch (InvocationTargetException e) {
            throw (RuntimeException) e.getCause();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    protected Object anything() {
        return _anything();
    }

    // exactly :o = :p ? (o.equals(p)) -> p
    protected Object exactly() {
        final Object o = apply("anything");
        final Object p = apply("anything");

        ensure(o.equals(p));
        return p;
    }

    protected Object seq() {
        final Collection items = (Collection) apply("anything");

        for (Object item : items) {
            prependInput(item);
            apply("exactly");
        }

        return items;
    }

    // end = ~anything
    protected Object end() {
        return _not(new Rule() {
            public Object execute() {
                return apply("anything");
            }
        });
    }
}
