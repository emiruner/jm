package tr.rimerun.jm.rule.text;

import tr.rimerun.jm.Parser;
import tr.rimerun.jm.Rule;

// digit = chr:d ?(Character.isDigit(d)) -> d
public class Digit implements Rule {
    public static final Digit Instance = new Digit();

    private Digit() {
    }

    public Object execute(Parser parser) {
        Character chr = (Character) parser.apply(Chr.Instance);
        parser.ensure(Character.isDigit(chr));
        return chr;
    }
}
