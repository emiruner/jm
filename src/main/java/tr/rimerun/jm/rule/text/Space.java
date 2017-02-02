package tr.rimerun.jm.rule.text;

import tr.rimerun.jm.Parser;
import tr.rimerun.jm.Rule;

// space = chr:ch ?(Character.isWhitespace(ch)) -> ch
public class Space implements Rule {
    public static final Space Instance = new Space();

    private Space() {
    }

    public Object execute(Parser parser) {
        Character chr = (Character) parser.apply(Chr.Instance);
        parser.ensure(Character.isWhitespace(chr));
        return chr;
    }
}
