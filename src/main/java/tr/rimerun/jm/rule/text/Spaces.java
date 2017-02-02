package tr.rimerun.jm.rule.text;

import tr.rimerun.jm.Parser;
import tr.rimerun.jm.Rule;

// spaces = space*
public class Spaces implements Rule {
    public static final Spaces Instance = new Spaces();

    private Spaces() {
    }

    public Object execute(Parser parser) {
        return parser._many(Space.Instance);
    }
}
