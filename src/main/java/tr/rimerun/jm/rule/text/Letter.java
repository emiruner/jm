package tr.rimerun.jm.rule.text;

import tr.rimerun.jm.Parser;
import tr.rimerun.jm.Rule;

// letter = chr:d ?(Character.isLetter(d)) -> d
public class Letter implements Rule {
    public static final Letter Instance = new Letter();

    private Letter() {
    }

    public Object execute(Parser parser) {
        Character chr = (Character) parser.apply(Chr.Instance);
        parser.ensure(Character.isLetter(chr));
        return chr;
    }
}
