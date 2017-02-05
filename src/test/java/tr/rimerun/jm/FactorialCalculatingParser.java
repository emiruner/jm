package tr.rimerun.jm;

import static tr.rimerun.jm.BaseRules.anything;
import static tr.rimerun.jm.BaseRules.exactly;
import static tr.rimerun.jm.TextRules.num;

// ometa FactorialCalculatingParser
public class FactorialCalculatingParser {
    // fact 0  =                -> 1
    // fact :n = fact(n - 1):m  -> n * m
    private static final Rule fact = new Rule() {
        public Object execute(Parser parser) {
            return parser._or(
                    new Rule() {
                        public Object execute(Parser parser) {
                            parser.applyWithArgs(exactly, 0);
                            return 1;
                        }
                    },
                    new Rule() {
                        public Object execute(Parser parser) {
                            Integer n = (Integer) parser.apply(anything);
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
            Integer n = (Integer) parser.apply(num);
            return parser.applyWithArgs(fact, n);
        }
    };
}
