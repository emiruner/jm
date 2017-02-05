package tr.rimerun.jm;

import static tr.rimerun.jm.BaseRules.exactly;
import static tr.rimerun.jm.TextRules.num;
import static tr.rimerun.jm.TextRules.spaces;
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
                               parser.apply(spaces);

                               Object t = parser.applyWithArgs(exactly, '-');
                               Object n = parser.apply(num);

                               return list("Expr", e, t, n);
                           }
                       }, num
            );
        }
    };
}
