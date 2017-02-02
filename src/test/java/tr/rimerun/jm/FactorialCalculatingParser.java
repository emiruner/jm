package tr.rimerun.jm;

import tr.rimerun.jm.rule.Anything;
import tr.rimerun.jm.rule.Exactly;
import tr.rimerun.jm.rule.text.Num;

// ometa FactorialCalculatingParser
@SuppressWarnings({"UnusedDeclaration"})
public class FactorialCalculatingParser {
    // fact 0  =                -> 1
    // fact :n = fact(n - 1):m  -> n * m
    private static final Rule fact = new Rule() {
        public Object execute(Parser parser) {
            return parser._or(
                    new Rule() {
                        public Object execute(Parser parser) {
                            parser.applyWithArgs(Exactly.Instance, 0);
                            return 1;
                        }
                    },
                    new Rule() {
                        public Object execute(Parser parser) {
                            Integer n = (Integer) parser.apply(Anything.Instance);
                            Integer m = (Integer) parser.applyWithArgs(fact, n - 1);

                            return n * m;
                        }
                    }
            );
        }
    };

    // start = num:n fact(n):f   -> f
    public static final Rule start = new Rule() {
        public Object execute(Parser parser) {
            Integer n = (Integer) parser.apply(Num.Instance);
            return parser.applyWithArgs(fact, n);
        }
    };
}
