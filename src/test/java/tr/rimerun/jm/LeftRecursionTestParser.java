package tr.rimerun.jm;

import static tr.rimerun.jm.Util.list;

// ometa LeftRecursionTestParser <: BaseTestParser
public class LeftRecursionTestParser extends BaseTestParser {
    public LeftRecursionTestParser(LinkedInputStream input) {
        super(input);
    }

    // expr = expr:e spaces '-':t num:n -> new Object[]{"Expr", e, t, n}
    //      | num:n                     -> n
    @SuppressWarnings({"UnusedDeclaration"})
    private Object expr() {
        return _or(new Rule() {
                       public Object call() {
                           Object e = apply("expr");
                           apply("spaces");
                           Object t = applyWithArgs("exactly", '-');
                           Object n = apply("num");

                           return list("Expr", e, t, n);
                       }
                   }, new Rule() {
                       public Object call() {
                           return apply("num");
                       }
                   }
        );
    }
}
