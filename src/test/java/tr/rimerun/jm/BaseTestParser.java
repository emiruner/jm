package tr.rimerun.jm;

@SuppressWarnings({"UnusedDeclaration"})
public class BaseTestParser extends CharacterOrientedBaseParser {
    public BaseTestParser(LinkedInputStream input) {
        super(input);
    }

    // rawNum = rawNum:n digit:d -> (((Integer) n) * 10 + Character.digit((Character) d, 10)
    //        | digit:d          -> (Character.digit((Character) d, 10)
    private Object rawNum() {
        return _or(new Rule() {
                       public Object execute() {
                           Object n = apply("rawNum");
                           Object d = apply("digit");

                           return ((Integer) n) * 10 + Character.digit((Character) d, 10);
                       }
                   }, new Rule() {
                       public Object execute() {
                           Object d = apply("digit");
                           return Character.digit((Character) d, 10);
                       }
                   }
        );
    }

    // num = spaces '-'?:minus rawNum:n -> (minus == null ? n : -1 * n)
    private Object num() {
        apply("spaces");

        Object minus = _opt(new Rule() {
            public Object execute() {
                return applyWithArgs("exactly", '-');
            }
        });

        Integer n = (Integer) apply("rawNum");

        return minus == null ? n : -1 * n;
    }
}
