package rme.jm;

import static java.util.Arrays.asList;

// ometa LeftRecursionTestParser
public class LeftRecursionTestParser {
    // expr = expr:e spaces '-':t num:n -> new Object[]{"Expr", e, t, n}
    //      | num:n                     -> n
    public static final Rule expr = new Rule() {
        public Object execute(Parser parser) {
            return parser._or(parser1 -> {
                Object e = parser1.apply(expr);
                parser1.apply(TextRules.spaces);

                Object t = parser1.applyWithArgs(BaseRules.exactly, '-');
                Object n = parser1.apply(TextRules.num);

                return asList("Expr", e, t, n);
            }, TextRules.num);
        }
    };
}
