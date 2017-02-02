package tr.rimerun.jm.rule.text;

import tr.rimerun.jm.Parser;
import tr.rimerun.jm.Rule;
import tr.rimerun.jm.rule.Anything;

// chr = :o ?(o instanceof Character) -> o
public class Chr implements Rule {
    public static final Chr Instance = new Chr();

    private Chr() {
    }

    public Object execute(Parser parser) {
        Object o = parser.apply(Anything.Instance);
        parser.ensure(o instanceof Character);
        return o;
    }
}
