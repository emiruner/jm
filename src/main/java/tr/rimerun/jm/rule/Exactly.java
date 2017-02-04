package tr.rimerun.jm.rule;

import tr.rimerun.jm.Parser;
import tr.rimerun.jm.Rule;

public class Exactly implements Rule {
    public static final Exactly Instance = new Exactly();

    private Exactly() {
    }

    public Object execute(Parser parser) {
        final Object o = parser.apply(Anything.Instance);
        final Object p = parser.apply(Anything.Instance);

        parser.ensure(o.equals(p), "expecting: '" + o + "' but found: '" + p + "'");

        return p;
    }
}
