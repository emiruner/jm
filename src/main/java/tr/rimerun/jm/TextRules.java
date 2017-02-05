package tr.rimerun.jm;

import java.util.ArrayList;

import static tr.rimerun.jm.BaseRules.*;
import static tr.rimerun.jm.RuleUtil.many;
import static tr.rimerun.jm.RuleUtil.manyOne;

public final class TextRules {
    // chr = :o ?(o instanceof Character) -> o
    public static final Rule chr = new Rule() {
        public Object execute(Parser parser) {
            Object o = parser.apply(anything);
            parser.ensure(o instanceof Character);
            return o;
        }
    };

    // digit = chr:d ?(Character.isDigit(d)) -> d
    public static final Rule digit = new Rule() {
        public Object execute(Parser parser) {
            Character chr = (Character) parser.apply(TextRules.chr);
            parser.ensure(Character.isDigit(chr));
            return chr;
        }
    };

    // letter = chr:d ?(Character.isLetter(d)) -> d
    public static final Rule letter = new Rule() {
        public Object execute(Parser parser) {
            Character chr = (Character) parser.apply(TextRules.chr);
            parser.ensure(Character.isLetter(chr));
            return chr;
        }
    };

    // rawNum = rawNum:n digit:d -> (((Integer) n) * 10 + Character.digit((Character) d, 10)
    //        | digit:d          -> (Character.digit((Character) d, 10)
    public static final Rule rawNum = new Rule() {
        public Object execute(Parser parser) {
            return parser._or(
                    new Rule() {
                        public Object execute(Parser parser) {
                            Object n = parser.apply(rawNum);
                            Object d = parser.apply(digit);

                            return ((Integer) n) * 10 + Character.digit((Character) d, 10);
                        }
                    }, new Rule() {
                        public Object execute(Parser parser) {
                            Object d = parser.apply(digit);
                            return Character.digit((Character) d, 10);
                        }
                    }
            );
        }
    };

    // space = chr:ch ?(Character.isWhitespace(ch)) -> ch
    public static final Rule space = new Rule() {
        public Object execute(Parser parser) {
            Character chr = (Character) parser.apply(TextRules.chr);
            parser.ensure(Character.isWhitespace(chr));
            return chr;
        }
    };

    // spaces = space*
    public static final Rule spaces = many(space);

    // num = spaces '-'?:minus rawNum:n -> (minus == null ? n : -1 * n)
    public static final Rule num = new Rule() {
        public Object execute(Parser parser) {
            parser.apply(spaces);

            Object minus = parser._opt(new Rule() {
                public Object execute(Parser parser) {
                    return parser.applyWithArgs(exactly, '-');
                }
            });

            Integer n = (Integer) parser.apply(rawNum);

            return minus == null ? n : -1 * n;
        }
    };

    // token :t = spaces seq(t) (space+ | end) -> t
    public static final Rule token = new Rule() {
        public Object execute(Parser parser) {
            final String value = (String) parser.apply(anything);

            parser.apply(spaces);

            final ArrayList<Character> chars = new ArrayList<Character>(value.length());

            for (char ch : value.toCharArray()) {
                chars.add(ch);
            }

            parser.applyWithArgs(seq, chars);
            parser._or(manyOne(space), end);

            return value;
        }
    };
}
