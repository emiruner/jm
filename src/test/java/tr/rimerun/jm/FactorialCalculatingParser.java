package tr.rimerun.jm;

// ometa FactorialCalculatingParser <: BaseTestParser
@SuppressWarnings({"UnusedDeclaration"})
public class FactorialCalculatingParser extends BaseTestParser {
    public FactorialCalculatingParser(LinkedInputStream input) {
        super(input);

        // fact 0  =                -> 1
        // fact :n = fact(n - 1):m  -> n * m
        addRule("fact", new Rule() {
            public Object execute() {
                return _or(
                        new Rule() {
                            public Object execute() {
                                applyWithArgs("exactly", 0);
                                return 1;
                            }
                        },
                        new Rule() {
                            public Object execute() {
                                Integer n = (Integer) apply("anything");
                                Integer m = (Integer) applyWithArgs("fact", n - 1);

                                return n * m;
                            }
                        }
                );
            }
        });

        // start = num:n fact(n):f   -> f
        addRule("start", new Rule() {
            public Object execute() {
                Integer n = (Integer) apply("num");
                return applyWithArgs("fact", n);
            }
        });
    }
}
