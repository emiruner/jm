package rme.jm;

// ometa LeftRecursionTestParser
public class LeftRecursionTestParser {
    // expr = expr:e spaces '-':t num:n -> new Object[]{"Expr", e, t, n}
    //      | num:n                     -> n
    public static final Rule expr = new Rule() {
        public Object execute(Parser parser) {
            return parser._or(new Rule() {
                           public Object execute(Parser parser) {
                               Object e = parser.apply(expr);
                               parser.apply(TextRules.spaces);

                               Object t = parser.applyWithArgs(BaseRules.exactly, '-');
                               Object n = parser.apply(TextRules.num);

                               return Util.list("Expr", e, t, n);
                           }
                       }, TextRules.num
            );
        }
    };
}
