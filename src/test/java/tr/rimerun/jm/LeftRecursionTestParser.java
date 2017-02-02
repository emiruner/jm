package tr.rimerun.jm;

import tr.rimerun.jm.rule.Exactly;
import tr.rimerun.jm.rule.text.Num;
import tr.rimerun.jm.rule.text.Spaces;

import static tr.rimerun.jm.Util.list;

// ometa LeftRecursionTestParser
public class LeftRecursionTestParser {
    // expr = expr:e spaces '-':t num:n -> new Object[]{"Expr", e, t, n}
    //      | num:n                     -> n
    public static final Rule expr = new Rule() {
        public Object execute(Parser parser) {
            return parser._or(new Rule() {
                           public Object execute(Parser parser) {
                               Object e = parser.apply(expr);
                               parser.apply(Spaces.Instance);

                               Object t = parser.applyWithArgs(Exactly.Instance, '-');
                               Object n = parser.apply(Num.Instance);

                               return list("Expr", e, t, n);
                           }
                       }, Num.Instance
            );
        }
    };
}
