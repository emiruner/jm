package rme.jm;

// ometa FactorialCalculatingParser
public class FactorialCalculatingParser {
    // fact 0  =                -> 1
    // fact :n = fact(n - 1):m  -> n * m
    private static final Rule fact = new Rule() {
        public Object execute(Parser parser) {
            return parser._or(
                    parser1 -> {
                        parser1.applyWithArgs(BaseRules.exactly, 0);
                        return 1;
                    },
                    parser2 -> {
                        Integer n = (Integer) parser2.apply(BaseRules.anything);
                        Integer m = (Integer) parser2.applyWithArgs(fact, n - 1);

                        return n * m;
                    }
            );
        }
    };

    // start = num:n fact(n):f   -> f
    public static final Rule start = parser -> {
        Integer n = (Integer) parser.apply(TextRules.num);
        return parser.applyWithArgs(fact, n);
    };
}
