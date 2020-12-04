package rme.jm;

import java.util.ArrayList;

import static rme.jm.RuleUtil.many;
import static rme.jm.RuleUtil.manyOne;

public final class TextRules {
    // chr = :o ?(o instanceof Character) -> o
    public static final Rule chr = parser -> {
        Object o = parser.apply(BaseRules.anything);
        parser.ensure(o instanceof Character);
        return o;
    };

    // digit = chr:d ?(Character.isDigit(d)) -> d
    public static final Rule digit = parser -> {
        Character chr = (Character) parser.apply(TextRules.chr);
        parser.ensure(Character.isDigit(chr));
        return chr;
    };

    // letter = chr:d ?(Character.isLetter(d)) -> d
    public static final Rule letter = parser -> {
        Character chr = (Character) parser.apply(TextRules.chr);
        parser.ensure(Character.isLetter(chr));
        return chr;
    };

    // rawNum = rawNum:n digit:d -> (((Integer) n) * 10 + Character.digit((Character) d, 10)
    //        | digit:d          -> (Character.digit((Character) d, 10)
    public static final Rule rawNum = new Rule() {
        public Object execute(Parser parser) {
            return parser._or(
                    parser1 -> {
                        Object n = parser1.apply(rawNum);
                        Object d = parser1.apply(digit);

                        return ((Integer) n) * 10 + Character.digit((Character) d, 10);
                    },
                    parser2 -> {
                        Object d = parser2.apply(digit);
                        return Character.digit((Character) d, 10);
                    }
            );
        }
    };

    // space = chr:ch ?(Character.isWhitespace(ch)) -> ch
    public static final Rule space = parser -> {
        Character chr = (Character) parser.apply(TextRules.chr);
        parser.ensure(Character.isWhitespace(chr));
        return chr;
    };

    // spaces = space*
    public static final Rule spaces = many(space);

    // num = spaces '-'?:minus rawNum:n -> (minus == null ? n : -1 * n)
    public static final Rule num = parser -> {
        parser.apply(spaces);

        Object minus = parser._opt(parser1 -> parser1.applyWithArgs(BaseRules.exactly, '-'));

        Integer n = (Integer) parser.apply(rawNum);

        return minus == null ? n : -1 * n;
    };

    // token :t = spaces seq(t) (space+ | end) -> t
    public static final Rule token = parser -> {
        final String value = (String) parser.apply(BaseRules.anything);

        parser.apply(spaces);

        final ArrayList<Character> chars = new ArrayList<>(value.length());

        for (char ch : value.toCharArray()) {
            chars.add(ch);
        }

        parser.applyWithArgs(BaseRules.seq, chars);
        parser._or(manyOne(space), BaseRules.end);

        return value;
    };
}
