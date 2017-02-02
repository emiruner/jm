package tr.rimerun.jm.rule.text;

import tr.rimerun.jm.Parser;
import tr.rimerun.jm.Rule;
import tr.rimerun.jm.rule.Exactly;

// num = spaces '-'?:minus rawNum:n -> (minus == null ? n : -1 * n)
public class Num implements Rule {
    public static final Num Instance = new Num();

    private Num() {
    }

    public Object execute(Parser parser) {
        parser.apply(Spaces.Instance);

        Object minus = parser._opt(new Rule() {
            public Object execute(Parser parser) {
                return parser.applyWithArgs(Exactly.Instance, '-');
            }
        });

        Integer n = (Integer) parser.apply(RawNum.Instance);

        return minus == null ? n : -1 * n;
    }
}
