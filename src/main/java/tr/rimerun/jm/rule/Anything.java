package tr.rimerun.jm.rule;

import tr.rimerun.jm.Parser;
import tr.rimerun.jm.Rule;

public class Anything implements Rule {
    public static final Anything Instance = new Anything();

    private Anything() {
    }

    public Object execute(Parser parser) {
        return parser._anything();
    }
}
