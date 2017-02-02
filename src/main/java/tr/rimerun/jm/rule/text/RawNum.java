package tr.rimerun.jm.rule.text;

import tr.rimerun.jm.Parser;
import tr.rimerun.jm.Rule;

// rawNum = rawNum:n digit:d -> (((Integer) n) * 10 + Character.digit((Character) d, 10)
//        | digit:d          -> (Character.digit((Character) d, 10)
public class RawNum implements Rule {
    public static final RawNum Instance = new RawNum();

    private RawNum() {
    }

    public Object execute(Parser parser) {
        return parser._or(
                new Rule() {
                    public Object execute(Parser parser) {
                        Object n = parser.apply(Instance);
                        Object d = parser.apply(Digit.Instance);

                        return ((Integer) n) * 10 + Character.digit((Character) d, 10);
                    }
                }, new Rule() {
                    public Object execute(Parser parser) {
                        Object d = parser.apply(Digit.Instance);
                        return Character.digit((Character) d, 10);
                    }
                }
        );
    }
}
