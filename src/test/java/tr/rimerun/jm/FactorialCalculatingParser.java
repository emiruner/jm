package tr.rimerun.jm;

// ometa FactorialCalculatingParser <: BaseTestParser
@SuppressWarnings({"UnusedDeclaration"})
public class FactorialCalculatingParser extends BaseTestParser {
    public FactorialCalculatingParser(LinkedInputStream input) {
        super(input);
    }

    // fact 0  =                -> 1
    // fact :n = fact(n - 1):m  -> n * m
    public Object fact() {
        return _or(
                new Rule() {
                    public Object execute() {
                        prependInput(0);
                        apply("exactly");
                        return 1;
                    }
                },
                new Rule() {
                    public Object execute() {
                        Integer n = (Integer) apply("anything");

                        prependInput(n - 1);
                        Integer m = (Integer) apply("fact");

                        return n * m;
                    }
                }
        );
    }

    // start = num:n fact(n):f   -> f
    public Object start() {
        Integer n = (Integer) apply("num");
        prependInput(n);

        return apply("fact");
    }
}
