package tr.rimerun.jm.rule;

import tr.rimerun.jm.Parser;
import tr.rimerun.jm.Rule;

import java.util.Collection;

public class Seq implements Rule {
    public static final Seq Instance = new Seq();

    private Seq() {
    }

    public Object execute(Parser parser) {
        final Collection items = (Collection) parser.apply(Anything.Instance);

        for (Object item : items) {
            parser.applyWithArgs(Exactly.Instance, item);
        }

        return items;
    }
}
