package rme.jm;

import java.util.Collection;

public final class BaseRules {
    public static final Rule anything = Parser::_anything;

    public static final Rule end = parser -> parser._not(anything);

    public static final Rule exactly = parser -> {
        final Object o = parser.apply(anything);
        final Object p = parser.apply(anything);

        parser.ensure(o.equals(p), "expecting: '" + o + "' but found: '" + p + "'");

        return p;
    };

    public static final Rule seq = parser -> {
        final Collection<?> items = (Collection<?>) parser.apply(anything);

        for (Object item : items) {
            parser.applyWithArgs(exactly, item);
        }

        return items;
    };
}
