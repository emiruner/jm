package tr.rimerun.jm;

public class RuleUtil {
    public static Rule many(final Rule inner) {
        return new Rule() {
            public Object execute(Parser parser) {
                return parser._many(inner);
            }
        };
    }

    public static Rule manyOne(final Rule inner) {
        return new Rule() {
            public Object execute(Parser parser) {
                return parser._many1(inner);
            }
        };
    }
}
