package tr.rimerun.jm;

import static tr.rimerun.jm.Util.list;

// ometa LeftRecursionTestParser <: BaseTestParser
public class LeftRecursionTestParser extends BaseTestParser {
    public LeftRecursionTestParser(LinkedInputStream input) {
        super(input);

        // expr = expr:e spaces '-':t num:n -> new Object[]{"Expr", e, t, n}
        //      | num:n                     -> n
        addRule("expr", new Rule() {
            public Object execute() {
                return _or(new Rule() {
                               public Object execute() {
                                   Object e = apply("expr");
                                   apply("spaces");

                                   prependInput('-');
                                   Object t = apply("exactly");
                                   Object n = apply("num");

                                   return list("Expr", e, t, n);
                               }
                           }, "num"
                );
            }
        });
    }
}
