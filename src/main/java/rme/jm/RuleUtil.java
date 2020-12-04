package rme.jm;

public class RuleUtil {
    public static Rule many(final Rule inner) {
        return parser -> parser._many(inner);
    }

    public static Rule manyOne(final Rule inner) {
        return parser -> parser._many1(inner);
    }
}
