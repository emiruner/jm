package tr.rimerun.jm;

import java.util.ArrayList;

@SuppressWarnings({"UnusedDeclaration"})
public class CharacterOrientedBaseParser extends BaseParser {
    public CharacterOrientedBaseParser(LinkedInputStream input) {
        super(input);
    }

    // token :t = spaces seq(t) (space+ | end) -> t
    private String token() {
        final String value = (String) apply("anything");
        apply("spaces");

        final ArrayList<Character> chars = new ArrayList<Character>(value.length());

        for (char ch : value.toCharArray()) {
            chars.add(ch);
        }

        applyWithArgs("seq", chars);

        _or(
                new Rule() {
                    public Object execute() {
                        return _many1("space");
                    }
                },
                new Rule() {
                    public Object execute() {
                        return apply("end");
                    }
                }
        );

        return value;
    }

    // chr = :o ?(o instanceof Character) -> o
    private Object chr() {
        return applyWithPred("anything", new Predicate() {
            public boolean eval(Object o) {
                return o instanceof Character;
            }
        });
    }

    // space = chr:ch ?(Character.isWhitespace(ch)) -> ch
    private Object space() {
        return applyWithPred("chr", new Predicate<Character>() {
            public boolean eval(Character ch) {
                return Character.isWhitespace(ch);
            }
        });
    }

    // spaces = space*
    private Object spaces() {
        return _many("space");
    }

    // digit = chr:d ?(Character.isDigit(d)) -> d
    private Object digit() {
        return applyWithPred("chr", new Predicate<Character>() {
            public boolean eval(Character d) {
                return Character.isDigit(d);
            }
        });
    }
}
