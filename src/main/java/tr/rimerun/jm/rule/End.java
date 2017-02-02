package tr.rimerun.jm.rule;

import tr.rimerun.jm.Parser;
import tr.rimerun.jm.Rule;

public class End implements Rule {
    public static final End Instance = new End();

    private End() {
    }

    public Object execute(Parser parser) {
        return parser._not(Anything.Instance);
    }
}
