package rme.jm;

import java.util.Collection;

public final class BaseRules {
    public static final Rule anything = new Rule() {
        public Object execute(Parser parser) {
            return parser._anything();
        }
    };

    public static final Rule end = new Rule() {
        public Object execute(Parser parser) {
            return parser._not(anything);
        }
    };

    public static final Rule exactly = new Rule() {
        public Object execute(Parser parser) {
            final Object o = parser.apply(anything);
            final Object p = parser.apply(anything);

            parser.ensure(o.equals(p), "expecting: '" + o + "' but found: '" + p + "'");

            return p;
        }
    };

    public static final Rule seq = new Rule() {
        public Object execute(Parser parser) {
            final Collection items = (Collection) parser.apply(anything);

            for (Object item : items) {
                parser.applyWithArgs(exactly, item);
            }

            return items;
        }
    };
}
